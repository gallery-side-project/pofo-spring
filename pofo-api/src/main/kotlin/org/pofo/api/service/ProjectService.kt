package org.pofo.api.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManager
import org.pofo.api.dto.ProjectCreateRequest
import org.pofo.api.dto.ProjectUpdateRequest
import org.pofo.common.exception.CustomException
import org.pofo.common.exception.ErrorCode
import org.pofo.domain.rds.domain.project.Project
import org.pofo.domain.rds.domain.project.ProjectList
import org.pofo.domain.rds.domain.project.Stack
import org.pofo.domain.rds.domain.project.repository.ProjectRepository
import org.pofo.domain.rds.domain.project.repository.StackRepository
import org.pofo.domain.rds.domain.project.vo.ProjectQuery
import org.pofo.domain.rds.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class ProjectService(
    private val entityManager: EntityManager,
    private val stackRepository: StackRepository,
    private val projectRepository: ProjectRepository,
) {
    fun findProjectById(projectId: Long): Project =
        projectRepository.findById(projectId) ?: throw CustomException(ErrorCode.PROJECT_NOT_FOUND)

    fun getAllProjectsByPagination(
        size: Int,
        cursor: Long,
    ): ProjectList = projectRepository.searchProjectWithCursor(size, cursor)

    @Transactional
    fun createProject(
        projectCreateRequest: ProjectCreateRequest,
        authorId: Long,
    ): Project {
        val author = entityManager.getReference(User::class.java, authorId)

        val imageUrls = projectCreateRequest.imageUrls ?: emptyList()
        val keyImageIndex =
            when {
                imageUrls.isEmpty() -> -1
                projectCreateRequest.keyImageIndex == null -> 0
                else -> projectCreateRequest.keyImageIndex
            }

        if (keyImageIndex >= imageUrls.size || (imageUrls.isNotEmpty() && keyImageIndex < 0)) {
            throw CustomException(ErrorCode.PROJECT_IMAGE_INDEX_ERROR)
        }

        val project =
            Project
                .builder()
                .title(projectCreateRequest.title)
                .Bio(projectCreateRequest.bio)
                .urls(projectCreateRequest.urls)
                .imageUrls(projectCreateRequest.imageUrls)
                .content(projectCreateRequest.content)
                .category(projectCreateRequest.category)
                .author(author)
                .build()
        return projectRepository.save(project)
    }

    @Transactional
    fun updateProject(
        projectUpdateRequest: ProjectUpdateRequest,
        authorId: Long,
    ): Project {
        // TODO: 유저 Author가 여러명 있는데 수정 권한을 다 주는게 맞는지 여부 확인 후 소유자 체크 옵션 추가
        val project =
            projectRepository.findById(projectUpdateRequest.projectId)
                ?: throw CustomException(ErrorCode.PROJECT_NOT_FOUND)

        val imageUrls = projectUpdateRequest.imageUrls ?: emptyList()
        val keyImageIndex =
            when {
                imageUrls.isEmpty() -> -1
                projectUpdateRequest.keyImageIndex == null -> 0
                else -> projectUpdateRequest.keyImageIndex
            }

        if (keyImageIndex >= imageUrls.size || (imageUrls.isNotEmpty() && keyImageIndex <= 0)) {
            throw CustomException(ErrorCode.PROJECT_IMAGE_INDEX_ERROR)
        }

        if (projectUpdateRequest.stackNames != null) {
            val foundStacks = stackRepository.findByNameIn(projectUpdateRequest.stackNames)
            logNotExistStacks(projectUpdateRequest.stackNames, foundStacks)
            project.updateStack(foundStacks)
        }

        project.update(
            projectUpdateRequest.title,
            projectUpdateRequest.bio,
            projectUpdateRequest.urls,
            projectUpdateRequest.imageUrls,
            projectUpdateRequest.keyImageIndex,
            projectUpdateRequest.content,
            projectUpdateRequest.category,
        )
        return projectRepository.save(project)
    }

    fun searchProject(projectQuery: ProjectQuery): ProjectList = projectRepository.searchProjectWithQuery(projectQuery)

    private fun logNotExistStacks(
        stackNames: List<String>,
        stacks: List<Stack>,
    ) {
        val stacksMap = stacks.associateBy { it.name }

        for (stack in stackNames) {
            val foundStack = stacksMap[stack]
            if (foundStack == null) {
                logger.warn { "A stack named [$stack] does not exist." }
            }
        }
    }
}
