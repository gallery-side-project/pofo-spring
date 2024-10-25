package org.pofo.api.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.pofo.api.dto.LoginRequest
import org.pofo.domain.user.User
import org.pofo.domain.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
internal class AuthenticationTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val userRepository: UserRepository,
    ) : DescribeSpec({
            extensions(SpringExtension)

            val objectMapper = jacksonObjectMapper()
            val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
            val fakeUser = User.create("test@org.com", "test")

            beforeSpec {
                val encodedPassword = passwordEncoder.encode(fakeUser.password)
                val fakeUserWithEncryptedPassword = User.create("test@org.com", encodedPassword)
                userRepository.save(fakeUserWithEncryptedPassword)
            }

            describe("로그인 요청") {
                context("이메일과 비밀번호가 제대로 주어졌을 때") {
                    val requestBody = LoginRequest(fakeUser.email, fakeUser.password)
                    val mvcResult =
                        mockMvc
                            .perform(
                                post("/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestBody)),
                            ).andReturn()
                    it("status 200과 유저 객체를 반환해야 한다.") {
                        mvcResult.response.status shouldBe HttpStatus.OK.value()
                    }
                }

                context("이메일이 제대로 주어지지 않았을 때") {
                    val requestBody = LoginRequest("wrong@org.com", "")
                    val mvcResult =
                        mockMvc
                            .perform(
                                post("/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestBody)),
                            ).andReturn()
                    it("status 401을 반환해야 한다.") {
                        mvcResult.response.status shouldBe HttpStatus.UNAUTHORIZED.value()
                    }
                }

                context("비밀번호가 제대로 주어지지 않았을 때") {
                    val requestBody = LoginRequest(fakeUser.email, "wrongPassword")
                    val mvcResult =
                        mockMvc
                            .perform(
                                post("/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestBody)),
                            ).andReturn()
                    it("status 401을 반환해야 한다.") {
                        mvcResult.response.status shouldBe HttpStatus.UNAUTHORIZED.value()
                    }
                }
            }
        })
