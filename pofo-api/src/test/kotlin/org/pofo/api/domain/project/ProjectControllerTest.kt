package org.pofo.api.domain.project

import io.kotest.core.spec.style.StringSpec
import org.pofo.api.common.fixture.ProjectFixture.Companion.createProject
import org.pofo.api.common.fixture.UserFixture
import org.pofo.api.domain.project.dto.ProjectCreateRequest
import org.pofo.api.domain.project.dto.ProjectResponse
import org.pofo.api.domain.project.dto.ProjectSearchRequest
import org.pofo.api.domain.project.dto.ProjectUpdateRequest
import org.pofo.api.domain.security.jwt.JwtService
import org.pofo.api.domain.security.jwt.JwtTokenData
import org.pofo.api.domain.user.UserService
import org.pofo.api.domain.user.dto.UserRegisterRequest
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
class ProjectControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val userService: UserService,
        private val projectService: ProjectService,
        private val jwtService: JwtService,
    ) : StringSpec({
            val user = UserFixture.createUser()
            val client: WebTestClient.Builder =
                MockMvcWebTestClient.bindTo(mockMvc).baseUrl("http://localhost:8080/graphql")
            lateinit var savedUser: User
            lateinit var accessToken: String

            beforeEach {
                savedUser =
                    userService.createUser(
                        UserRegisterRequest(
                            email = user.email,
                            password = user.password,
                            username = user.username,
                        ),
                    )
                accessToken = jwtService.generateAccessToken(JwtTokenData(savedUser))
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
                        stackNames = null,
                    ),
                    authorId = authorId,
                )

            "프로젝트 생성" {
                val project = createProject()
                val projectCreateRequest =
                    ProjectCreateRequest(
                        title = project.title,
                        bio = project.bio,
                        content = project.content,
                    )

                val graphQlTester =
                    HttpGraphQlTester.builder(client).header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken").build()

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

            "프로젝트 검색 - ID를 이용한 단일 검색" {
                val savedProject = saveProject(createProject(), savedUser.id)

                val graphQlTester = HttpGraphQlTester.builder(client).build()

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

            "프로젝트 검색 - 페이지네이션" {
                val savedProjects = mutableListOf<ProjectResponse>()
                repeat(30) { idx ->
                    savedProjects.add(saveProject(createProject(title = "Test Project $idx"), savedUser.id))
                }

                val firstPageRequest =
                    ProjectSearchRequest(
                        title = null,
                        categories = null,
                        stackNames = null,
                        authorName = null,
                        page = 0,
                        size = 15,
                    )

                val graphQlTester = HttpGraphQlTester.builder(client).build()

                val expectedFirstPageProjects = savedProjects.subList(15, 30).reversed()

                graphQlTester
                    .documentName("searchProject")
                    .variable("projectSearchRequest", firstPageRequest)
                    .execute()
                    .path("searchProject.count")
                    .entity(Int::class.java)
                    .isEqualTo(15)
                    .path("searchProject.projects[*].title")
                    .entityList(String::class.java)
                    .containsExactly(
                        *expectedFirstPageProjects.map { project -> project.title }.toTypedArray(),
                    ).path("searchProject.hasNext")
                    .entity(Boolean::class.java)
                    .isEqualTo(true)

                val secondPageRequest =
                    ProjectSearchRequest(
                        title = null,
                        categories = null,
                        stackNames = null,
                        authorName = null,
                        page = 1,
                        size = 15,
                    )

                val expectedSecondPageProjects = savedProjects.subList(0, 15).reversed()

                graphQlTester
                    .documentName("searchProject")
                    .variable("projectSearchRequest", secondPageRequest)
                    .execute()
                    .path("searchProject.count")
                    .entity(Int::class.java)
                    .isEqualTo(15)
                    .path("searchProject.projects[*].title")
                    .entityList(String::class.java)
                    .containsExactly(
                        *expectedSecondPageProjects.map { project -> project.title }.toTypedArray(),
                    ).path("searchProject.hasNext")
                    .entity(Boolean::class.java)
                    .isEqualTo(false)
            }

            "프로젝트 검색 - 작성자 검색" {
                val otherSavedUser =
                    userService.createUser(
                        UserRegisterRequest(
                            email = "2_${user.email}",
                            password = user.password,
                            username = "2_${user.username}",
                        ),
                    )
                val savedProjects = mutableListOf<ProjectResponse>()
                repeat(30) { idx ->
                    savedProjects.add(
                        saveProject(
                            createProject(title = "Test Project $idx"),
                            if (idx % 2 == 0) {
                                savedUser.id
                            } else {
                                otherSavedUser.id
                            },
                        ),
                    )
                }

                val firstUserRequest =
                    ProjectSearchRequest(
                        title = null,
                        categories = null,
                        stackNames = null,
                        authorName = savedUser.username,
                        page = 0,
                        size = 30,
                    )

                val graphQlTester = HttpGraphQlTester.builder(client).build()

                val expectedFirstUserProjects =
                    savedProjects
                        .filter { project -> project.authorName == savedUser.username }
                        .reversed()

                graphQlTester
                    .documentName("searchProject")
                    .variable("projectSearchRequest", firstUserRequest)
                    .execute()
                    .path("searchProject.count")
                    .entity(Int::class.java)
                    .isEqualTo(15)
                    .path("searchProject.projects[*].title")
                    .entityList(String::class.java)
                    .containsExactly(
                        *expectedFirstUserProjects.map { project -> project.title }.toTypedArray(),
                    ).path("searchProject.hasNext")
                    .entity(Boolean::class.java)
                    .isEqualTo(false)
            }

            "프로젝트 수정" {
                val savedProject = saveProject(createProject(), savedUser.id)
                val projectUpdateRequest =
                    ProjectUpdateRequest(
                        projectId = savedProject.id,
                        title = "new title",
                        bio = "new bio",
                    )

                val graphQlTester =
                    HttpGraphQlTester.builder(client).header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken").build()

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
        })
