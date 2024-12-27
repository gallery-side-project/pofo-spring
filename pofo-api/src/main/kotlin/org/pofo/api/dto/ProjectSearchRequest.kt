package org.pofo.api.dto

import org.pofo.domain.rds.domain.project.ProjectCategory

data class ProjectSearchRequest(
    val title: String?,
    val category: ProjectCategory?,
    val stackNames: List<String>?,
    val page: Int = 0,
    val size: Int = 30,
)
