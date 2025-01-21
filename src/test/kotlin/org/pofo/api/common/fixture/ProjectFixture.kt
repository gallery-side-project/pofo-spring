package org.pofo.api.common.fixture

import org.pofo.api.domain.project.Project
import org.pofo.api.domain.user.User

class ProjectFixture {
    companion object {
        fun createProject(
            title: String = "Test Project",
            bio: String = "Test Project Bio",
            content: String = "Test Project Content",
            urls: List<String> = listOf(),
            imageUrls: List<String> = listOf(),
            keyImageIndex: Int = 0,
            likes: Int = 0,
            author: User,
        ): Project =
            Project
                .builder()
                .title(title)
                .bio(bio)
                .content(content)
                .urls(urls)
                .imageUrls(imageUrls)
                .keyImageIndex(keyImageIndex)
                .likes(likes)
                .author(author)
                .build()
    }
}
