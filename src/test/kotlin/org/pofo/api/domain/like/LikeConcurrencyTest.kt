package org.pofo.api.domain.like

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.pofo.api.common.fixture.ProjectFixture
import org.pofo.api.domain.project.Project
import org.pofo.api.domain.project.repository.ProjectRepository
import org.pofo.api.domain.user.User
import org.pofo.api.domain.user.UserRepository
import org.pofo.api.domain.user.UserRole
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
@ActiveProfiles("test")
class LikeConcurrencyTest(
    @Autowired private val projectRepository: ProjectRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val likeService: LikeService,
    @Autowired private val transactionTemplate: TransactionTemplate,
) : DescribeSpec({
        lateinit var users: List<User>
        lateinit var project: Project

        beforeEach {
            users =
                userRepository.saveAll(
                    List(31) {
                        User
                            .builder()
                            .email("user$it@example.com")
                            .password("testPassword")
                            .role(UserRole.ROLE_USER)
                            .username("user$it")
                            .build()
                    },
                )
            project = projectRepository.save(ProjectFixture.createProject(author = users.last()))
        }

        describe("좋아요 동시성 테스트") {
            it("동시 요청에서도 좋아요 수가 정확히 관리된다") {
                val likeCount = 30

                // 모든 스레드가 동시에 시작되도록 제어해서 동시성 처리 테스트
                val latch = CountDownLatch(likeCount)
                val executor = Executors.newFixedThreadPool(likeCount)

                users.subList(0, likeCount).forEach { user ->
                    executor.submit {
                        try {
                            likeService.likeProject(user.id, project.id)
                        } catch (ex: Exception) {
                            println("Exception for User ID: ${user.id}, Project ID: ${project.id} -> ${ex.message}")
                        } finally {
                            latch.countDown()
                        }
                    }
                }

                // 스레드가 모두 끝날때 까지 대기
                latch.await()
                executor.shutdown()

                transactionTemplate.execute {
                    val resultProject = projectRepository.findByIdOrNull(project.id)
                    resultProject.shouldNotBeNull()
                    resultProject.likes shouldBe likeCount
                }
            }
        }
    })
