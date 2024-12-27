package org.pofo.api.dto

import org.pofo.domain.rds.domain.project.Project
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
