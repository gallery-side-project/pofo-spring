package org.pofo.api.service

import jakarta.persistence.EntityManager
import lombok.extern.slf4j.Slf4j
import org.pofo.api.dto.CreateProjectRequest
import org.pofo.common.exception.CustomException
import org.pofo.common.exception.ErrorCode
import org.pofo.domain.rds.domain.project.Project
import org.pofo.domain.rds.domain.project.ProjectCategory
import org.pofo.domain.rds.domain.project.ProjectList
import org.pofo.domain.rds.domain.project.repository.ProjectRepository
import org.pofo.domain.rds.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
@Transactional(readOnly = true)
class ProjectService(
    private val entityManager: EntityManager,
    private val projectRepository: ProjectRepository,
) {
    fun findProjectById(projectId: Long): Project =
        projectRepository.findById(projectId) ?: throw CustomException(ErrorCode.PROJECT_NOT_FOUND)

    fun getAllProjectsByPagination(
        size: Int,
        cursor: Long,
    ): ProjectList = projectRepository.searchProjectWithCursor(size, cursor)

    @Transactional
    fun createProject(createProjectRequest: CreateProjectRequest): Project {
        val author = entityManager.getReference(User::class.java, createProjectRequest.authorId)
        val project =
            Project
                .builder()
                .title(createProjectRequest.title)
                .Bio(createProjectRequest.bio)
                .urls(createProjectRequest.urls)
                .imageUrls(createProjectRequest.imageUrls)
                .content(createProjectRequest.content)
                .category(createProjectRequest.category)
                .stacks(createProjectRequest.stacks)
                .author(author)
                .build()
        return projectRepository.save(project)
    }

    @Transactional
    fun updateProject(
        projectId: Long,
        title: String?,
        bio: String?,
        urls: List<String>?,
        imageUrls: List<String>?,
        content: String?,
        category: ProjectCategory?,
        authorId: Long,
    ): Project {
        // TODO: 유저 Author가 여러명 있는데 수정 권한을 다 주는게 맞는지 여부 확인 후 소유자 체크 옵션 추가
        var project = projectRepository.findById(projectId) ?: throw CustomException(ErrorCode.PROJECT_NOT_FOUND)
        project = project.update(title, bio, urls, imageUrls, content, category)

        return projectRepository.save(project)
    }
}
