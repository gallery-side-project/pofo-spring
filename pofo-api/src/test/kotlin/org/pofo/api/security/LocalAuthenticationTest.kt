package org.pofo.api.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.servlet.http.Cookie
import org.pofo.api.dto.LoginRequest
import org.pofo.api.dto.RegisterRequest
import org.pofo.api.service.UserService
import org.pofo.domain.domain.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
internal class LocalAuthenticationTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val userService: UserService,
    ) : DescribeSpec({
            extensions(SpringExtension)

            val rememberMeCookieName = "pofo_rmm"
            val objectMapper = jacksonObjectMapper()

            val fakeUser = User.create("test@org.com", "test")
            beforeSpec {
                val registerRequest = RegisterRequest(fakeUser.email, fakeUser.password)
                userService.createUser(registerRequest)
            }

            fun localLogin(
                requestBody: LoginRequest,
                rememberMe: Boolean = false,
            ): MvcResult =
                mockMvc
                    .perform(
                        post("/auth/local")
                            .param("remember-me", if (rememberMe) "true" else "false")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)),
                    ).andDo(MockMvcResultHandlers.print())
                    .andReturn()

            describe("로그인 시") {
                context("이메일과 비밀번호가 제대로 주어졌을 때") {
                    val requestBody = LoginRequest(fakeUser.email, fakeUser.password)
                    val mvcResult = localLogin(requestBody)
                    it("세션과 status 200을 반환해야 한다.") {
                        mvcResult.response.status shouldBe HttpStatus.OK.value()
                        mvcResult.request.session!!
                            .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)
                            .shouldNotBeNull()
                    }
                }

                context("이메일이 제대로 주어지지 않았을 때") {
                    val requestBody = LoginRequest("wrong@org.com", "")
                    val mvcResult = localLogin(requestBody)
                    it("status 401을 반환해야 한다.") {
                        mvcResult.response.status shouldBe HttpStatus.UNAUTHORIZED.value()
                        mvcResult.request.session!!
                            .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)
                            .shouldBeNull()
                    }
                }

                context("비밀번호가 제대로 주어지지 않았을 때") {
                    val requestBody = LoginRequest(fakeUser.email, "wrongPassword")
                    val mvcResult = localLogin(requestBody)
                    it("status 401을 반환해야 한다.") {
                        mvcResult.response.status shouldBe HttpStatus.UNAUTHORIZED.value()
                        mvcResult.request.session!!
                            .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)
                            .shouldBeNull()
                    }
                }

                context("리멤버 미 파라미터가 주어졌을 때") {
                    val requestBody = LoginRequest(fakeUser.email, fakeUser.password)
                    val mvcResult = localLogin(requestBody, true)

                    it("세션과 리멤버 미 쿠키를 같이 반환해야 한다.") {
                        mvcResult.response.status shouldBe HttpStatus.OK.value()
                        mvcResult.request.session!!
                            .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)
                            .shouldNotBeNull()
                        mvcResult.response
                            .getHeaders("Set-Cookie")
                            .find { cookie ->
                                cookie.startsWith(rememberMeCookieName)
                            }.shouldNotBeNull()
                    }
                }
            }

            describe("인증이 필요한 요청 시") {
                context("세션 쿠키가 없고, 리멤버 미 쿠키가 주어졌을 때") {
                    val requestBody = LoginRequest(fakeUser.email, fakeUser.password)
                    val loginMvcResult = localLogin(requestBody, true)

                    val rememberMeCookieValue =
                        loginMvcResult.response
                            .getCookie(rememberMeCookieName)!!
                            .value
                    val mvcResult =
                        mockMvc
                            .perform(
                                get("/user/me")
                                    .cookie(Cookie(rememberMeCookieName, rememberMeCookieValue)),
                            ).andReturn()

                    it("리멤버 미 쿠키가 재설정 되어야 한다.") {
                        mvcResult.response.status shouldBe HttpStatus.OK.value()
                        mvcResult.response
                            .getHeaders("Set-Cookie")
                            .find { cookie ->
                                cookie.startsWith(rememberMeCookieName)
                            }.shouldNotBeNull()
                    }
                }
            }
        })
