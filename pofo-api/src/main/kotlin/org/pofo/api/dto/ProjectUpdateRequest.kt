package org.pofo.api.dto

import org.pofo.domain.rds.domain.project.ProjectCategory

data class ProjectUpdateRequest(
    val projectId: Long,
    val title: String? = null,
    val bio: String? = null,
    val urls: List<String>? = null,
    val imageUrls: List<String>? = null,
    val content: String? = null,
    val category: ProjectCategory? = null,
    val stackNames: List<String>? = null,
)
