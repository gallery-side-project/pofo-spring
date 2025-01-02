package org.pofo.api.domain.project.dto

import org.pofo.domain.rds.domain.project.Category

data class ProjectUpdateRequest(
    val projectId: Long,
    val title: String? = null,
    val bio: String? = null,
    val urls: List<String>? = null,
    val imageUrls: List<String>? = null,
    val keyImageIndex: Int? = null,
    val content: String? = null,
    val categories: List<Category>? = null,
    val stackNames: List<String>? = null,
)
