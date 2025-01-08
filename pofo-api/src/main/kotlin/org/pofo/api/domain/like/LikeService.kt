package org.pofo.api.domain.like

import org.hibernate.StaleObjectStateException
import org.pofo.common.exception.CustomException
import org.pofo.common.exception.ErrorCode
import org.pofo.domain.rds.domain.like.Like
import org.pofo.domain.rds.domain.like.LikeRepository
import org.pofo.domain.rds.domain.project.repository.ProjectRepository
import org.pofo.domain.rds.domain.user.UserRepository
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LikeService(
    private val likeRepository: LikeRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun likeProject(
        userId: Long,
        projectId: Long,
    ): Int =
        retryOptimisticLock {
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
            likeRepository.saveAndFlush(like) // 즉시 저장 및 반영
            project.increaseLikes()
            project.likes
        }

    @Transactional
    fun unlikeProject(
        userId: Long,
        projectId: Long,
    ): Int =
        retryOptimisticLock {
            val user =
                userRepository.findById(userId)
                    ?: throw CustomException(ErrorCode.USER_NOT_FOUND)

            val project =
                projectRepository.findById(projectId)
                    ?: throw CustomException(ErrorCode.PROJECT_NOT_FOUND)

            val like =
                likeRepository.findByUserAndProject(user, project)
                    ?: throw CustomException(ErrorCode.LIKE_NOT_FOUND)

            likeRepository.delete(like)
            project.decreaseLikes()
            project.likes
        }

    private fun <T> retryOptimisticLock(action: () -> T): T {
        var attempts = 0
        val maxRetries = 5
        while (true) {
            try {
                return action()
            } catch (ex: OptimisticLockingFailureException) {
                if (++attempts > maxRetries) {
                    throw CustomException(ErrorCode.LIKE_FAILED)
                }
                Thread.sleep(50)
            } catch (ex: StaleObjectStateException) {
                if (++attempts > maxRetries) {
                    throw CustomException(ErrorCode.LIKE_FAILED)
                }
                Thread.sleep(50)
            }
        }
    }
}
