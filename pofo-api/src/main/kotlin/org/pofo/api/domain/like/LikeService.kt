package org.pofo.api.domain.like

import org.hibernate.StaleObjectStateException
import org.pofo.common.exception.CustomException
import org.pofo.common.exception.ErrorCode
import org.pofo.domain.rds.domain.like.Like
import org.pofo.domain.rds.domain.like.LikeRepository
import org.pofo.domain.rds.domain.project.repository.ProjectRepository
import org.pofo.domain.rds.domain.user.UserRepository
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LikeService(
    private val likeRepository: LikeRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
) {
    @Retryable(
        value = [OptimisticLockingFailureException::class, StaleObjectStateException::class],
        maxAttempts = 10,
        backoff = Backoff(delay = 50, multiplier = 2.0),
    )
    @Transactional
    fun likeProject(
        userId: Long,
        projectId: Long,
    ): Int {
        val user =
            userRepository.findById(userId)
                ?: throw CustomException(ErrorCode.USER_NOT_FOUND)

        val project =
            projectRepository.findById(projectId)
                ?: throw CustomException(ErrorCode.PROJECT_NOT_FOUND)

        if (likeRepository.existsByUserAndProject(user, project)) {
            throw CustomException(ErrorCode.ALREADY_LIKED_PROJECT)
        }

        val like =
            Like
                .builder()
                .project(project)
                .user(user)
                .build()
        project.increaseLikes()
        projectRepository.save(project)
        likeRepository.save(like)
        return project.likes
    }

    @Retryable(
        value = [OptimisticLockingFailureException::class, StaleObjectStateException::class],
        maxAttempts = 10,
        backoff = Backoff(delay = 50, multiplier = 2.0),
    )
    @Transactional
    fun unlikeProject(
        userId: Long,
        projectId: Long,
    ): Int {
        val user =
            userRepository.findById(userId)
                ?: throw CustomException(ErrorCode.USER_NOT_FOUND)

        val project =
            projectRepository.findById(projectId)
                ?: throw CustomException(ErrorCode.PROJECT_NOT_FOUND)

        val like =
            likeRepository.findByUserAndProject(user, project)
                ?: throw CustomException(ErrorCode.LIKE_NOT_FOUND)

        project.decreaseLikes()
        projectRepository.save(project)
        likeRepository.delete(like)
        return project.likes
    }

    @Deprecated(message = "이유를 모르겠으나 제대로 작동안하는 재시도 로직. 참고용으로 남김")
    private fun <T> retryOptimisticLock(action: () -> T): T {
        var attempts = 0
        val maxRetries = 10
        var waitTime = 50L
        while (true) {
            try {
                return action()
            } catch (ex: OptimisticLockingFailureException) {
                if (++attempts > maxRetries) {
                    throw CustomException(ErrorCode.LIKE_FAILED)
                }
            } catch (ex: StaleObjectStateException) {
                if (++attempts > maxRetries) {
                    throw CustomException(ErrorCode.LIKE_FAILED)
                }
            }
            Thread.sleep(waitTime)
        }
    }
}
