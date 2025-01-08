package org.pofo.api.domain.like

import org.pofo.domain.rds.domain.like.LikeRepository
import org.pofo.domain.rds.domain.project.repository.ProjectRepository
import org.pofo.domain.rds.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeService(
    private val likeRepository: LikeRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun likeProject(
        userId: Long,
        projectId: Long,
    ) {
        val user =
            userRepository
                .findById(userId)
                .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }
        val project =
            projectRepository
                .findById(projectId)
                .orElseThrow { IllegalArgumentException("프로젝트를 찾을 수 없습니다.") }

        if (likeRepository.existsByUserAndProject(user, project)) {
            throw IllegalStateException("이미 좋아요를 누른 프로젝트입니다.")
        }

        val like = Like.createLike(user, project)
        likeRepository.save(like)
        project.increaseLikes()
    }

    @Transactional
    fun unlikeProject(
        userId: Long,
        projectId: Long,
    ) {
        val user =
            userRepository
                .findById(userId)
                .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }
        val project =
            projectRepository
                .findById(projectId)
                .orElseThrow { IllegalArgumentException("프로젝트를 찾을 수 없습니다.") }

        val like =
            likeRepository
                .findByUserAndProject(user, project)
                .orElseThrow { IllegalStateException("좋아요가 존재하지 않습니다.") }

        likeRepository.delete(like)
        project.decreaseLikes()
    }

    @Transactional(readOnly = true)
    fun getLikedProjects(userId: Long): List<Project> {
        val user =
            userRepository
                .findById(userId)
                .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        return likeRepository.findLikedProjectsByUser(user)
    }
}
