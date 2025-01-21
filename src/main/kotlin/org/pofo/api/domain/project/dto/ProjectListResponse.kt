package org.pofo.api.domain.project.dto

import org.pofo.api.domain.project.Project
import org.springframework.data.domain.Slice

data class ProjectListResponse(
    val projects: List<ProjectResponse>,
    val hasNext: Boolean,
    val count: Int,
) {
    companion object {
        fun from(projectSlice: Slice<Project>): ProjectListResponse =
            ProjectListResponse(
                projectSlice.content.map { ProjectResponse.from(it) },
                projectSlice.hasNext(),
                projectSlice.count(),
            )
    }
}
