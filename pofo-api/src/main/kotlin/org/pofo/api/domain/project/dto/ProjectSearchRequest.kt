package org.pofo.api.domain.project.dto

import org.pofo.domain.rds.domain.project.Category

data class ProjectSearchRequest(
    val title: String?,
    val categories: List<Category>?,
    val stackNames: List<String>?,
    val authorName: String?,
    val page: Int = 0,
    val size: Int = 30,
)
