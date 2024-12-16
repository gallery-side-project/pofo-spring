package org.pofo.api.controller

import org.pofo.api.security.PrincipalDetails
import org.pofo.domain.rds.domain.project.Project
import org.pofo.domain.rds.domain.project.ProjectCategory
import org.pofo.domain.rds.domain.project.ProjectList
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller

@Controller
class ProjectController(
    private val projectService: org.pofo.api.service.ProjectService,
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
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
    ): Project =
        projectService.createProject(
            title,
            bio,
            urls,
            imageUrls,
            content,
            category,
            principalDetails.jwtTokenData.userId,
        )

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
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
    ): Project =
        projectService.updateProject(
            projectId,
            title,
            bio,
            urls,
            imageUrls,
            content,
            category,
            principalDetails.jwtTokenData.userId,
        )
}
