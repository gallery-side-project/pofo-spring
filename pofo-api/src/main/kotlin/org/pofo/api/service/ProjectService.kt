package org.pofo.api.service

import lombok.extern.slf4j.Slf4j
import org.pofo.common.error.CustomError
import org.pofo.common.error.ErrorType
import org.pofo.domain.project.Project
import org.pofo.domain.project.ProjectCategory
import org.pofo.domain.project.ProjectList
import org.pofo.domain.project.repository.ProjectRepository
import org.pofo.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
@Transactional(readOnly = true)
class ProjectService(
    private val projectRepository: ProjectRepository,
) {
    fun findProjectById(projectId: Long): Project =
        projectRepository.findById(projectId) ?: throw CustomError(ErrorType.PROJECT_NOT_FOUND)

    fun getAllProjectsByPagination(
        size: Int,
        cursor: Long,
    ): ProjectList = projectRepository.searchProjecWithCursor(size, cursor)

    @Transactional
    fun createProject(
        title: String,
        bio: String?,
        urls: List<String>?,
        imageUrls: List<String>?,
        content: String,
        category: ProjectCategory,
        author: User,
    ): Project {
        val project =
            Project
                .builder()
                .title(title)
                .Bio(bio)
                .urls(urls)
                .imageUrls(imageUrls)
                .content(content)
                .category(category)
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
        author: User,
    ): Project {
        // TODO: 유저 Author가 여러명 있는데 수정 권한을 다 주는게 맞는지 여부 확인 후 소유자 체크 옵션 추가
        var project = projectRepository.findById(projectId) ?: throw CustomError(ErrorType.PROJECT_NOT_FOUND)
        project = project.update(title, bio, urls, imageUrls, content, category)

        return projectRepository.save(project)
    }
}
