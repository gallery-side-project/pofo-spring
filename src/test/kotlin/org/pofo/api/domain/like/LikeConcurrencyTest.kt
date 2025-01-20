package org.pofo.api.domain.like

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.pofo.api.common.exception.CustomException
import org.pofo.api.common.exception.ErrorCode
import org.pofo.api.common.fixture.ProjectFixture
import org.pofo.api.domain.project.Project
import org.pofo.api.domain.project.repository.ProjectRepository
import org.pofo.api.domain.user.User
import org.pofo.api.domain.user.UserRepository
import org.pofo.api.domain.user.UserRole
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
@ActiveProfiles("test")
class LikeConcurrencyTest(
    @Autowired private val projectRepository: ProjectRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val likeService: LikeService,
): DescribeSpec({
    lateinit var users: List<User>
    lateinit var project: Project

    beforeEach {
        users = userRepository.saveAll(List(31) {
            User
                .builder()
                .email("user$it@example.com")
                .password("testPassword")
                .role(UserRole.ROLE_USER)
                .username("user$it")
                .build()
        })
        project = projectRepository.save(ProjectFixture.createProject(author = users.last()))
    }

    afterEach {
        projectRepository.deleteAll()
        userRepository.deleteAll()
    }

    describe("좋아요 동시성 테스트") {
        it("동시 요청에서도 좋아요 수가 정확히 관리된다") {
            val likeCount = 30

            // 모든 스레드가 동시에 시작되도록 제어해서 동시성 처리 테스트
            val latch = CountDownLatch(likeCount)
            val executor = Executors.newFixedThreadPool(likeCount)

            users.subList(0, 30).forEach { user ->
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

            // DB에 조회 한번 해봐서 제대로 값이 나왔는지 확인
            val updatedProject =
                projectRepository.findByIdOrNull(project.id)
                    ?: throw CustomException(ErrorCode.PROJECT_NOT_FOUND)
            updatedProject.likes shouldBe likeCount
        }
    }
})
