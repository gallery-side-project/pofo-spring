package org.pofo.api.dto

import org.pofo.domain.rds.domain.project.ProjectCategory
import org.pofo.domain.rds.domain.project.ProjectStack

data class CreateProjectRequest(
    val title: String,
    val bio: String?,
    val urls: List<String>?,
    val keyImageIndex: Int?,
    val imageUrls: List<String>?,
    val content: String,
    val category: ProjectCategory,
    val stacks: List<ProjectStack>?,
    val authorId: Long,
)
