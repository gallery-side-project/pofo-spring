package org.pofo.api.domain.project

import org.pofo.api.domain.project.dto.ProjectCreateRequest
import org.pofo.api.domain.project.dto.ProjectListResponse
import org.pofo.api.domain.project.dto.ProjectResponse
import org.pofo.api.domain.project.dto.ProjectSearchRequest
import org.pofo.api.domain.project.dto.ProjectUpdateRequest
import org.pofo.api.domain.security.PrincipalDetails
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
    ): ProjectResponse =
        projectService
            .findProjectById(
                projectId,
            )

    @QueryMapping
    fun getAllProjectsByPagination(
        @Argument cursor: Long?,
        @Argument size: Int,
    ): ProjectListResponse =
        projectService
            .getAllProjectsByPagination(
                size,
                cursor
                    ?: 0,
            )

    @PreAuthorize(
        "isAuthenticated()",
    )
    @MutationMapping
    fun createProject(
        @Argument projectCreateRequest: ProjectCreateRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
    ): ProjectResponse =
        projectService
            .createProject(
                projectCreateRequest,
                principalDetails.jwtTokenData.userId,
            )

    @PreAuthorize(
        "isAuthenticated()",
    )
    @MutationMapping
    fun updateProject(
        @Argument projectUpdateRequest: ProjectUpdateRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
    ): ProjectResponse =
        projectService
            .updateProject(
                projectUpdateRequest,
                principalDetails.jwtTokenData.userId,
            )

    @QueryMapping
    fun searchProject(
        @Argument projectSearchRequest: ProjectSearchRequest,
    ): ProjectListResponse =
        projectService
            .searchProject(
                projectSearchRequest,
            )
}
