package org.pofo.api.domain.project.dto

import org.pofo.domain.rds.domain.project.Project

data class ProjectResponse(
    val id: Long,
    val title: String,
    val bio: String?,
    val urls: List<String>?,
    val imageUrls: List<String>?,
    val keyImageIndex: Int,
    val content: String,
    val isApproved: Boolean,
    val likes: Int,
    val categories: List<String>?,
    val stacks: List<String>?,
    val authorName: String,
) {
    companion object {
        fun from(project: Project): ProjectResponse =
            ProjectResponse(
                project.id!!,
                project.title,
                project.bio,
                project.urls,
                project.imageUrls,
                project.keyImageIndex,
                project.content,
                project.isApproved,
                project.likes,
                project.categories
                    .map {
                        it.category.name
                    },
                project.stacks
                    .map {
                        it.stack.name
                    },
                project.author.email,
            )
    }
}
