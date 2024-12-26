package org.pofo.api.fixture

import org.pofo.domain.rds.domain.project.Project
import org.pofo.domain.rds.domain.project.ProjectCategory
import org.pofo.domain.rds.domain.project.ProjectStack

class ProjectFixture {
    companion object {
        fun createProject(): Project =
            Project
                .builder()
                .title("Luminia")
                .Bio("줄거리로 애니를 찾아주고, 애니를 검색하고 리뷰를 달 수 있는 플랫폼입니다.")
                .content("취업좀 시켜줘라")
                .urls(listOf("https://github.com/mclub4"))
                .imageUrls(listOf("https://avatars.githubusercontent.com/u/55117706?v=4"))
                .category(ProjectCategory.CATEGORY_A)
                .stacks(listOf(ProjectStack.ELK))
                .isApproved(false)
                .build()
    }
}
