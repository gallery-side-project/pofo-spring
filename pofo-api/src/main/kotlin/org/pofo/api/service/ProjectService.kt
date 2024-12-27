package org.pofo.api.service

import jakarta.persistence.EntityManager
import lombok.extern.slf4j.Slf4j
import org.pofo.api.dto.CreateProjectRequest
import org.pofo.api.dto.UpdateProjectRequest
import org.pofo.common.exception.CustomException
import org.pofo.common.exception.ErrorCode
import org.pofo.domain.rds.domain.project.Project
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

        val imageUrls = createProjectRequest.imageUrls ?: emptyList()
        val keyImageIndex = createProjectRequest.keyImageIndex ?: -1

        if (keyImageIndex >= imageUrls.size || (imageUrls.isNotEmpty() && keyImageIndex <= 0)) {
            throw CustomException(ErrorCode.PROJECT_IMAGE_INDEX_ERROR)
        }

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
    fun updateProject(updateProjectRequest: UpdateProjectRequest): Project {
        // TODO: 유저 Author가 여러명 있는데 수정 권한을 다 주는게 맞는지 여부 확인 후 소유자 체크 옵션 추가
        var project =
            projectRepository.findById(updateProjectRequest.projectId)
                ?: throw CustomException(ErrorCode.PROJECT_NOT_FOUND)

        val imageUrls = updateProjectRequest.imageUrls ?: emptyList()
        val keyImageIndex = updateProjectRequest.keyImageIndex ?: -1

        if (keyImageIndex >= imageUrls.size || (imageUrls.isNotEmpty() && keyImageIndex <= 0)) {
            throw CustomException(ErrorCode.PROJECT_IMAGE_INDEX_ERROR)
        }

        project =
            project.update(
                updateProjectRequest.title,
                updateProjectRequest.bio,
                updateProjectRequest.urls,
                updateProjectRequest.keyImageIndex,
                updateProjectRequest.imageUrls,
                updateProjectRequest.content,
                updateProjectRequest.category,
                updateProjectRequest.stacks,
            )
        return projectRepository.save(project)
    }
}
