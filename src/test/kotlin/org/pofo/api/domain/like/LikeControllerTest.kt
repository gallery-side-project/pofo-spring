package org.pofo.api.domain.like

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.pofo.api.common.exception.ErrorCode
import org.pofo.api.common.fixture.ProjectFixture
import org.pofo.api.common.fixture.UserFixture
import org.pofo.api.common.util.Version
import org.pofo.api.domain.project.Project
import org.pofo.api.domain.project.repository.ProjectRepository
import org.pofo.api.domain.security.jwt.JwtService
import org.pofo.api.domain.security.jwt.JwtTokenData
import org.pofo.api.domain.user.User
import org.pofo.api.domain.user.UserRepository
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
internal class LikeControllerTest
@Autowired
constructor(
    private val mockMvc: MockMvc,
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val likeRepository: LikeRepository,
    private val jwtService: JwtService,
) : DescribeSpec({
    lateinit var user: User
    lateinit var project: Project
    lateinit var accessToken: String

    beforeEach {
        user =
            userRepository.save(UserFixture.createUser())
        project =
            projectRepository.save(ProjectFixture.createProject(author = user))
        accessToken =
            jwtService.generateAccessToken(
                JwtTokenData(
                    user,
                ),
            )
    }

    describe("좋아요 등록") {
        it("성공적으로 좋아요를 등록한다") {
            mockMvc
                .post("${Version.V1}/like/${project.id}") {
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
            mockMvc
                .post("${Version.V1}/like/${project.id}") {
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
                .delete("${Version.V1}/like/${project.id}") {
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
                .delete("${Version.V1}/like/${project.id}") {
                    contentType = MediaType.APPLICATION_JSON
                    headers { set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken") }
                }.andExpect {
                    status { isBadRequest() }
                    jsonPath("$.code") { value(ErrorCode.LIKE_NOT_FOUND.code) }
                }
        }
    }
})
