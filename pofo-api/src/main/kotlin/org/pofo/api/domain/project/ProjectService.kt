package org.pofo.api.domain.project

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManager
import org.pofo.api.domain.project.dto.ProjectCreateRequest
import org.pofo.api.domain.project.dto.ProjectListResponse
import org.pofo.api.domain.project.dto.ProjectResponse
import org.pofo.api.domain.project.dto.ProjectSearchRequest
import org.pofo.api.domain.project.dto.ProjectUpdateRequest
import org.pofo.common.exception.CustomException
import org.pofo.common.exception.ErrorCode
import org.pofo.domain.rds.domain.project.Project
import org.pofo.domain.rds.domain.project.Stack
import org.pofo.domain.rds.domain.project.repository.ProjectRepository
import org.pofo.domain.rds.domain.project.repository.StackRepository
import org.pofo.domain.rds.domain.user.User
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProjectService(
    private val entityManager: EntityManager,
    private val stackRepository: StackRepository,
    private val projectRepository: ProjectRepository,
) {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    fun findProjectById(projectId: Long): ProjectResponse {
        val foundProject =
            projectRepository.findById(projectId) ?: throw CustomException(ErrorCode.PROJECT_NOT_FOUND)
        return ProjectResponse.from(foundProject)
    }

    fun getAllProjectsByPagination(
        size: Int,
        cursor: Long,
    ): ProjectListResponse {
        val foundProjects = projectRepository.searchProjectWithCursor(size, cursor)
        return ProjectListResponse.from(foundProjects)
    }

    @Transactional
    fun createProject(
        projectCreateRequest: ProjectCreateRequest,
        authorId: Long,
    ): ProjectResponse {
        val author = entityManager.getReference(User::class.java, authorId)

        val imageUrls = projectCreateRequest.imageUrls ?: emptyList()
        val keyImageIndex =
            when {
                imageUrls.isEmpty() -> -1
                projectCreateRequest.keyImageIndex == null -> 0
                else -> projectCreateRequest.keyImageIndex
            }

        if (keyImageIndex >= imageUrls.size ||
            (
                imageUrls.isNotEmpty() &&
                    keyImageIndex <
                    0
            )
        ) {
            throw CustomException(
                ErrorCode.PROJECT_IMAGE_INDEX_ERROR,
            )
        }

        val project =
            Project
                .builder()
                .title(projectCreateRequest.title)
                .bio(projectCreateRequest.bio)
                .urls(projectCreateRequest.urls)
                .imageUrls(projectCreateRequest.imageUrls)
                .keyImageIndex(keyImageIndex)
                .content(projectCreateRequest.content)
                .isApproved(projectCreateRequest.isApproved)
                .author(author)
                .build()
        val savedProject = projectRepository.save(project)

        if (projectCreateRequest.stackNames != null) {
            val foundStacks =
                stackRepository
                    .findByNameIn(
                        projectCreateRequest.stackNames,
                    )
            logNotExistStacks(
                projectCreateRequest.stackNames,
                foundStacks,
            )
            project
                .updateStack(
                    foundStacks,
                )
        }

        if (projectCreateRequest.categories !=
            null
        ) {
            project
                .updateCategories(
                    projectCreateRequest.categories,
                )
        }

        return ProjectResponse
            .from(
                savedProject,
            )
    }

    @Transactional
    fun updateProject(
        projectUpdateRequest: ProjectUpdateRequest,
        authorId: Long,
    ): ProjectResponse {
        // TODO: 유저 Author가 여러명 있는데 수정 권한을 다 주는게 맞는지 여부 확인 후 소유자 체크 옵션 추가
        val project =
            projectRepository.findById(
                projectUpdateRequest.projectId,
            )
                ?: throw CustomException(
                    ErrorCode.PROJECT_NOT_FOUND,
                )

        val imageUrls =
            projectUpdateRequest.imageUrls
                ?: emptyList()
        val keyImageIndex =
            when {
                imageUrls
                    .isEmpty() -> -1

                projectUpdateRequest.keyImageIndex ==
                    null -> 0

                else -> projectUpdateRequest.keyImageIndex
            }

        if (keyImageIndex >=
            imageUrls.size ||
            (
                imageUrls.isNotEmpty() &&
                    keyImageIndex <=
                    0
            )
        ) {
            throw CustomException(
                ErrorCode.PROJECT_IMAGE_INDEX_ERROR,
            )
        }

        val updatedProject =
            project
                .update(
                    projectUpdateRequest.title,
                    projectUpdateRequest.bio,
                    projectUpdateRequest.urls,
                    projectUpdateRequest.imageUrls,
                    projectUpdateRequest.keyImageIndex,
                    projectUpdateRequest.content,
                )
        if (projectUpdateRequest.stackNames !=
            null
        ) {
            val foundStacks =
                stackRepository.findByNameIn(projectUpdateRequest.stackNames)
            logNotExistStacks(
                projectUpdateRequest.stackNames,
                foundStacks,
            )
            project.updateStack(foundStacks)
        }

        if (projectUpdateRequest.categories != null) {
            project.updateCategories(projectUpdateRequest.categories)
        }

        return ProjectResponse
            .from(
                updatedProject,
            )
    }

    fun searchProject(projectSearchRequest: ProjectSearchRequest): ProjectListResponse {
        val pageRequest =
            PageRequest.of(
                projectSearchRequest.page,
                projectSearchRequest.size,
            )
        val projectSlice =
            projectRepository
                .searchProjectWithQuery(
                    projectSearchRequest.title,
                    projectSearchRequest.categories,
                    projectSearchRequest.stackNames,
                    projectSearchRequest.authorName,
                    pageRequest,
                )
        return ProjectListResponse.from(projectSlice)
    }

    private fun logNotExistStacks(
        stackNames: List<String>,
        stacks: List<Stack>,
    ) {
        val stacksMap =
            stacks.associateBy { it.name }

        for (stack in stackNames) {
            val foundStack =
                stacksMap[stack]
            if (foundStack == null) {
                logger.warn { "A stack named [$stack] does not exist." }
            }
        }
    }
}
