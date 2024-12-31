package org.pofo.api.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.pofo.api.dto.ProjectCreateRequest
import org.pofo.api.dto.ProjectResponse
import org.pofo.api.dto.ProjectUpdateRequest
import org.pofo.api.dto.RegisterRequest
import org.pofo.api.fixture.ProjectFixture.Companion.createProject
import org.pofo.api.fixture.UserFixture
import org.pofo.api.security.jwt.JwtService
import org.pofo.api.security.jwt.JwtTokenData
import org.pofo.api.service.ProjectService
import org.pofo.api.service.UserService
import org.pofo.domain.rds.domain.project.Project
import org.pofo.domain.rds.domain.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.graphql.test.tester.HttpGraphQlTester
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.client.MockMvcWebTestClient
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
internal class ProjectControllerTest
    @Autowired
    constructor(
        mockMvc: MockMvc,
        private val userService: UserService,
        private val projectService: ProjectService,
        private val jwtService: JwtService,
    ) {
        val user: User = UserFixture.createUser()
        val client: WebTestClient.Builder =
            MockMvcWebTestClient
                .bindTo(mockMvc)
                .baseUrl("http://localhost:8080/graphql")
        lateinit var savedUser: User
        lateinit var accessToken: String

        @BeforeEach
        fun setUp() {
            savedUser = userService.createUser(RegisterRequest(user.email, user.password))
            accessToken =
                jwtService.generateAccessToken(
                    JwtTokenData(savedUser),
                )
        }

        @Test
        fun createProjectSuccess() {
            val project = createProject()
            val projectCreateRequest =
                ProjectCreateRequest(
                    title = project.title,
                    bio = project.bio,
                    content = project.content,
                )

            val graphQlTester =
                HttpGraphQlTester
                    .builder(client)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                    .build()

            graphQlTester
                .documentName("createProject")
                .variable("projectCreateRequest", projectCreateRequest)
                .execute()
                .path("createProject.title")
                .entity(String::class.java)
                .isEqualTo(project.title)
                .path("createProject.content")
                .entity(String::class.java)
                .isEqualTo(project.content)
        }

        @Test
        fun getProjectById() {
            // given
            val savedProject = saveProject(createProject(), savedUser.id)

            val graphQlTester =
                HttpGraphQlTester
                    .builder(client)
                    .build()

            // when & then
            graphQlTester
                .documentName("getProjectById")
                .variable("projectId", savedProject.id)
                .execute()
                .path("projectById.id")
                .entity(Long::class.java)
                .isEqualTo(savedProject.id)
                .path("projectById.title")
                .entity(String::class.java)
                .isEqualTo(savedProject.title)
        }

        @Test
        @DisplayName("페이지 네이션 적은 것 테스트")
        fun getAllProjectsByPagination() {
            // given
            val savedProjects = mutableListOf<ProjectResponse>()
            repeat(3) {
                savedProjects.add(saveProject(createProject(), savedUser.id))
            }

            val graphQlTester =
                HttpGraphQlTester
                    .builder(client)
                    .build()

            // when & then
            graphQlTester
                .documentName("getAllProjectsByPagination")
                .variable("cursor", savedProjects.last().id)
                .variable("size", 2)
                .execute()
                .path("getAllProjectsByPagination.count")
                .entity(Int::class.java)
                .isEqualTo(2)
                .path("getAllProjectsByPagination.projects[*].title")
                .entityList(String::class.java)
                .containsExactly(savedProjects[1].title, savedProjects[0].title)
                .path("getAllProjectsByPagination.hasNext")
                .entity(Boolean::class.java)
                .isEqualTo(false)
        }

        @Test
        @DisplayName("2번째 페이지 데이터 검증")
        fun getSecondPageProjectsByPaginationTest() {
            // given
            val savedProjects = mutableListOf<ProjectResponse>()
            repeat(20) {
                savedProjects.add(saveProject(createProject(), savedUser.id))
            }

            val graphQlTester =
                HttpGraphQlTester
                    .builder(client)
                    .build()

            val firstPageCursor = savedProjects[savedProjects.size - 5].id
            val expectedSecondPageProjects = savedProjects.subList(10, 15).reversed()

            // when & then
            graphQlTester
                .documentName("getAllProjectsByPagination")
                .variable("cursor", firstPageCursor)
                .variable("size", 5)
                .execute()
                .path("getAllProjectsByPagination.count")
                .entity(Int::class.java)
                .isEqualTo(5)
                .path("getAllProjectsByPagination.projects[*].title")
                .entityList(String::class.java)
                .containsExactly(*expectedSecondPageProjects.map { it.title }.toTypedArray())
                .path("getAllProjectsByPagination.hasNext")
                .entity(Boolean::class.java)
                .isEqualTo(true)
        }

        @Test
        fun updateProject() {
            // given
            val savedProject = saveProject(createProject(), savedUser.id)
            val projectUpdateRequest =
                ProjectUpdateRequest(
                    projectId = savedProject.id,
                    title = "new title",
                    bio = "new bio",
                )

            val graphQlTester =
                HttpGraphQlTester
                    .builder(client)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                    .build()

            graphQlTester
                .documentName("updateProject")
                .variable("projectUpdateRequest", projectUpdateRequest)
                .execute()
                .path("updateProject.title")
                .entity(String::class.java)
                .isEqualTo(projectUpdateRequest.title!!)
                .path("updateProject.bio")
                .entity(String::class.java)
                .isEqualTo(projectUpdateRequest.bio!!)
        }

        fun saveProject(
            project: Project,
            authorId: Long,
        ): ProjectResponse =
            projectService.createProject(
                ProjectCreateRequest(
                    title = project.title,
                    bio = project.bio,
                    content = project.content,
                    urls = project.urls,
                    keyImageIndex = 0,
                    imageUrls = project.imageUrls,
                    category = project.category,
                    stackNames = null,
                ),
                authorId = authorId,
            )
    }
