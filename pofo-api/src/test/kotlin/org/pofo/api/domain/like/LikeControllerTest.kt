package org.pofo.api.domain.like

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.pofo.api.domain.security.jwt.JwtService
import org.pofo.api.domain.security.jwt.JwtTokenData
import org.pofo.api.fixture.ProjectFixture
import org.pofo.api.fixture.UserFixture
import org.pofo.common.exception.CustomException
import org.pofo.common.exception.ErrorCode
import org.pofo.common.response.Version
import org.pofo.domain.rds.domain.like.Like
import org.pofo.domain.rds.domain.like.LikeRepository
import org.pofo.domain.rds.domain.project.Project
import org.pofo.domain.rds.domain.project.repository.ProjectRepository
import org.pofo.domain.rds.domain.user.User
import org.pofo.domain.rds.domain.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LikeControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val userRepository: UserRepository,
        private val projectRepository: ProjectRepository,
        private val likeRepository: LikeRepository,
        private val jwtService: JwtService,
        private val likeService: LikeService,
    ) : DescribeSpec({
            extensions(
                SpringExtension,
            )

            val objectMapper =
                jacksonObjectMapper()

            lateinit var user: User
            lateinit var project: Project
            lateinit var accessToken: String

            beforeEach {
                user = userRepository.save(UserFixture.createUser())
                project =
                    projectRepository.save(ProjectFixture.createProject())

                accessToken =
                    jwtService.generateAccessToken(
                        JwtTokenData(
                            user,
                        ),
                    )
            }

            describe("좋아요 등록") {
                it("성공적으로 좋아요를 등록한다") {
                    val result =
                        mockMvc
                            .post("${Version.V1}/likes/${project.id}") {
                                contentType = MediaType.APPLICATION_JSON
                                headers { set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken") }
                            }.andExpect {
                                status { isOk() }
                                jsonPath("$.data.likes") { value(1) }
                            }

                    likeRepository.existsByUserAndProject(user, project) shouldBe true
                }

                it("중복된 좋아요 등록 시 실패한다") {
                    // Given
                    likeRepository.save(
                        Like
                            .builder()
                            .user(user)
                            .project(project)
                            .build(),
                    )

                    // When
                    val result =
                        mockMvc
                            .post("${Version.V1}/likes/${project.id}") {
                                contentType = MediaType.APPLICATION_JSON
                                headers { set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken") }
                            }.andExpect {
                                status { isBadRequest() }
                                jsonPath("$.code") { value(ErrorCode.ALREADY_LIKED_PROJECT.code) }
                            }
                }
            }

            describe("좋아요 해제") {
                it("성공적으로 좋아요를 해제한다") {
                    // Given
                    likeRepository.save(
                        Like
                            .builder()
                            .user(user)
                            .project(project)
                            .build(),
                    )

                    // When
                    mockMvc
                        .delete("${Version.V1}/likes/${project.id}") {
                            contentType = MediaType.APPLICATION_JSON
                            headers { set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken") }
                        }.andExpect {
                            status { isOk() }
                            jsonPath("$.data.likes") { value(0) }
                        }

                    // Then
                    likeRepository.existsByUserAndProject(user, project) shouldBe false
                }

                it("좋아요가 없는 상태에서 해제 시 실패한다") {
                    mockMvc
                        .delete("${Version.V1}/likes/${project.id}") {
                            contentType = MediaType.APPLICATION_JSON
                            headers { set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken") }
                        }.andExpect {
                            status { isBadRequest() }
                            jsonPath("$.code") { value(ErrorCode.LIKE_NOT_FOUND.code) }
                        }
                }
            }

            describe("좋아요 동시성 테스트") {
                it("동시 요청에서도 좋아요 수가 정확히 관리된다") {
                    val likeCount = 1

                    // 모든 스레드가 동시에 시작되도록 제어해서 동시성 처리 테스트
                    val latch = CountDownLatch(likeCount)
                    val executor = Executors.newFixedThreadPool(likeCount)

                    val newUser =
                        userRepository.saveAndFlush(
                            User
                                .builder()
                                .email("test@naver.com")
                                .password("123gjs21@d")
                                .username("testnamerr")
                                .build(),
                        )
                    val newProject =
                        projectRepository.save(ProjectFixture.createProject())

                    val test = userRepository.findById(newUser.id)

                    repeat(likeCount) {
                        executor.submit {
                            try {
                                likeService.likeProject(newUser.id, newProject.id)
                            } catch (ex: Exception) {
                                println(
                                    "User ID: ${test.id}, Project ID: ${newProject.id} - Exception in thread ${Thread.currentThread().name}: ${ex.message}",
                                )
                            } finally {
                                latch.countDown()
                            }
                        }
                    }

                    // 스레드가 모두 끝날때 까지 대기
                    latch.await()
                    executor.shutdown()

                    // DB에 조회 한번 해봐서 제대로 값이 나왔는지 확인
                    val updatedProject =
                        projectRepository.findById(newProject.id!!)
                            ?: throw CustomException(ErrorCode.PROJECT_NOT_FOUND)
                    updatedProject.likes shouldBe likeCount
                }
            }
        })
