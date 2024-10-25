package org.pofo.api.controller

import org.pofo.api.service.ProjectService
import org.pofo.domain.project.Project
import org.pofo.domain.project.ProjectCategory
import org.pofo.domain.project.ProjectList
import org.pofo.domain.user.User
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller

@Controller
class ProjectController(
    private val projectService: ProjectService,
) {
    @QueryMapping
    fun projectById(
        @Argument projectId: Long,
    ): Project = projectService.findProjectById(projectId)

    @QueryMapping
    fun getAllProjectsByPagination(
        @Argument cursor: Long,
        @Argument size: Int,
    ): ProjectList = projectService.getAllProjectsByPagination(size, cursor)

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    fun createProject(
        @Argument title: String,
        @Argument bio: String?,
        @Argument urls: List<String>?,
        @Argument imageUrls: List<String>?,
        @Argument content: String,
        @Argument category: ProjectCategory,
        @AuthenticationPrincipal user: User,
    ): Project = projectService.createProject(title, bio, urls, imageUrls, content, category, user)

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    fun updateProject(
        @Argument projectId: Long,
        @Argument title: String?,
        @Argument bio: String?,
        @Argument urls: List<String>?,
        @Argument imageUrls: List<String>?,
        @Argument content: String?,
        @Argument category: ProjectCategory?,
        @AuthenticationPrincipal user: User,
    ): Project = projectService.updateProject(projectId, title, bio, urls, imageUrls, content, category, user)
}
