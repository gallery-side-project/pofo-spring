package org.pofo.api.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.pofo.api.dto.LoginRequest
import org.pofo.api.dto.RegisterRequest
import org.pofo.api.fixture.UserFixture
import org.pofo.api.security.jwt.JwtService
import org.pofo.api.security.jwt.JwtTokenData
import org.pofo.api.service.UserService
import org.pofo.common.exception.ErrorCode
import org.pofo.common.response.Version
import org.pofo.domain.rds.domain.user.User
import org.pofo.domain.redis.domain.accessToken.BannedAccessTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
internal class UserControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val userService: UserService,
        private val jwtService: JwtService,
        private val bannedAccessTokenRepository: BannedAccessTokenRepository,
    ) : DescribeSpec({
            extensions(SpringExtension)

            val objectMapper = jacksonObjectMapper()
            val user: User = UserFixture.createUser()

            describe("회원 가입 시") {
                val requestBody = RegisterRequest(user.email, user.password)

                it("유저 생성에 성공하고, 유저 조회에 성공해야 한다.") {
                    val resultActions =
                        mockMvc.post(Version.V1 + "/user") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(requestBody)
                        }
                    resultActions.andExpect {
                        status { isOk() }
                    }
                    val findUser = userService.getUserByEmail(requestBody.email)
                    findUser.id.shouldNotBeNull()
                    findUser.email shouldBe user.email
                    findUser.role shouldBe user.role
                }

                context("중복된 사용자가 있을 때") {
                    beforeEach {
                        userService.createUser(RegisterRequest(user.email, user.password))
                    }

                    it("유저 생성이 실패해야 한다.") {
                        mockMvc
                            .post(Version.V1 + "/user") {
                                contentType = MediaType.APPLICATION_JSON
                                content = objectMapper.writeValueAsString(requestBody)
                            }.andExpect {
                                status { isBadRequest() }
                                jsonPath("$.code") { value(ErrorCode.USER_EXISTS.code) }
                            }
                    }
                }
            }

            describe("로그인 시") {
                fun jwtLogin(requestBody: LoginRequest): ResultActionsDsl =
                    mockMvc.post(Version.V1 + "/user/login") {
                        contentType = MediaType.APPLICATION_JSON
                        content = objectMapper.writeValueAsString(requestBody)
                    }

                beforeEach {
                    userService.createUser(RegisterRequest(user.email, user.password))
                }

                context("이메일과 비밀번호가 제대로 주어졌을 때") {
                    it("엑세스 토큰을 반환하고, 리프레쉬 토큰을 쿠키에 설정해야 한다.") {
                        val requestBody = LoginRequest(user.email, user.password)
                        val resultActions = jwtLogin(requestBody)
                        resultActions.andExpect {
                            status { isOk() }
                            cookie {
                                exists(UserController.REFRESH_COOKIE_NAME)
                            }
                            jsonPath("$.data.accessToken") { exists() }
                        }
                    }
                }

                context("이메일이 제대로 주어지지 않았을 때") {
                    it("로그인 실패 에러를 반환해야 한다.") {
                        val requestBody = LoginRequest("wrong@org.com", "")
                        jwtLogin(requestBody).andExpect {
                            status { isUnauthorized() }
                            jsonPath("$.code") { value(ErrorCode.USER_LOGIN_FAILED.code) }
                        }
                    }
                }

                context("비밀번호가 제대로 주어지지 않았을 때") {
                    it("로그인 실패 에러를 반환해야 한다.") {
                        val requestBody = LoginRequest(user.email, "wrongPassword")
                        jwtLogin(requestBody).andExpect {
                            status { isUnauthorized() }
                            jsonPath("$.code") { value(ErrorCode.USER_LOGIN_FAILED.code) }
                        }
                    }
                }
            }

            describe("로그아웃 시") {
                it("엑세스 토큰을 벤하고, 리프레쉬 토큰을 지워야 한다.") {
                    val savedUser = userService.createUser(RegisterRequest(user.email, user.password))
                    val accessToken = jwtService.generateAccessToken(JwtTokenData(savedUser))

                    mockMvc
                        .post(Version.V1 + "/user/logout") {
                            contentType = MediaType.APPLICATION_JSON
                            headers {
                                set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                            }
                        }.andExpect {
                            status { isOk() }
                            cookie {
                                exists(UserController.REFRESH_COOKIE_NAME)
                                maxAge(UserController.REFRESH_COOKIE_NAME, 0)
                                value(UserController.REFRESH_COOKIE_NAME, "")
                            }
                        }
                    val bannedAccessTokenOptional = bannedAccessTokenRepository.findById(savedUser.id)
                    bannedAccessTokenOptional.isPresent shouldBe true
                    bannedAccessTokenOptional.get().value shouldBe accessToken
                }
            }

            describe("내 정보 조회 시") {
                lateinit var accessToken: String

                beforeEach {
                    val savedUser = userService.createUser(RegisterRequest(user.email, user.password))
                    accessToken = jwtService.generateAccessToken(JwtTokenData(savedUser))
                }

                it("내 정보가 반환된다.") {
                    mockMvc
                        .get(Version.V1 + "/user/me") {
                            headers {
                                set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                            }
                        }.andExpect {
                            status { isOk() }
                            jsonPath("$.data.email") { value(user.email) }
                            jsonPath("$.data.password") { doesNotExist() }
                            jsonPath("$.data.role") { value(user.role.name) }
                        }
                }
            }
        })
