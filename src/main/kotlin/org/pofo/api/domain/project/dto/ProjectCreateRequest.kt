package org.pofo.api.domain.project.dto

import org.pofo.api.domain.project.Category

data class ProjectCreateRequest(
    val title: String,
    val bio: String? = null,
    val urls: List<String>? = null,
    val keyImageIndex: Int? = null,
    val imageUrls: List<String>? = null,
    val content: String,
    val categories: List<Category>? = null,
    val stackNames: List<String>? = null,
    val isApproved: Boolean = false,
)
