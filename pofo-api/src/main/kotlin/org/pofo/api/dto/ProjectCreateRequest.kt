package org.pofo.api.dto

import org.pofo.domain.rds.domain.project.ProjectCategory

data class ProjectCreateRequest(
    val title: String,
    val bio: String? = null,
    val urls: List<String>? = null,
    val keyImageIndex: Int? = null,
    val imageUrls: List<String>? = null,
    val content: String,
    val category: ProjectCategory? = null,
    val stackNames: List<String>? = null,
    val isApproved: Boolean = false,
)
