package org.pofo.api.controller

import org.pofo.api.dto.ProjectCreateRequest
import org.pofo.api.dto.ProjectUpdateRequest
import org.pofo.api.security.PrincipalDetails
import org.pofo.domain.rds.domain.project.Project
import org.pofo.domain.rds.domain.project.ProjectList
import org.pofo.domain.rds.domain.project.vo.ProjectQuery
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
        @Argument cursor: Long?,
        @Argument size: Int,
    ): ProjectList = projectService.getAllProjectsByPagination(size, cursor ?: 0)

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    fun createProject(
        @Argument projectCreateRequest: ProjectCreateRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
    ): Project =
        projectService.createProject(
            projectCreateRequest,
            principalDetails.jwtTokenData.userId,
        )

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    fun updateProject(
        @Argument projectUpdateRequest: ProjectUpdateRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
    ): Project =
        projectService.updateProject(
            projectUpdateRequest,
            principalDetails.jwtTokenData.userId,
        )

    @QueryMapping
    fun searchProject(
        @Argument projectQuery: ProjectQuery,
    ): ProjectList = projectService.searchProject(projectQuery)
}
