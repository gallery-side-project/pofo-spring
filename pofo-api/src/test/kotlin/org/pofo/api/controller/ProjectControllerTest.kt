package org.pofo.api.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.pofo.api.dto.ProjectCreateRequest
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
                    urls = project.urls,
                    imageUrls = project.imageUrls,
                    category = project.category,
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
        fun getAllProjectsByPagination() {
            // given
            val savedProjects = mutableListOf<Project>()
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
                .path("getAllProjectsByPagination.projectCount")
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
        ): Project =
            projectService.createProject(
                ProjectCreateRequest(
                    title = project.title,
                    bio = project.bio,
                    content = project.content,
                    urls = project.urls,
                    imageUrls = project.imageUrls,
                    category = project.category,
                    stackNames = null,
                ),
                authorId = authorId,
            )
    }
