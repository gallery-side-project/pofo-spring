package org.pofo.api.domain.stack

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.called
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.pofo.api.common.fixture.StackFixture
import org.pofo.api.domain.security.jwt.JwtAuthenticationFilter
import org.pofo.api.domain.stack.dto.StackInsertRequest
import org.pofo.common.exception.ErrorCode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.post

@WebMvcTest(
    controllers = [StackController::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, OAuth2ClientAutoConfiguration::class],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [JwtAuthenticationFilter::class],
        ),
    ],
)
@ActiveProfiles("test")
internal class StackControllerTest(
    @Autowired val mockMvc: MockMvc,
    @MockkBean val stackService: StackService,
) : DescribeSpec({
        val objectMapper = jacksonObjectMapper()

        describe("POST /tech-stack - 단일 스택 삽입 시") {
            context("이름만 보내면") {
                it("단일 스택 저장에 성공하고, status 201을 응답한다.") {
                    val stackInsertRequest = StackInsertRequest(name = "Kotlin", imageUrl = null)

                    every { stackService.insertStack(any(StackInsertRequest::class)) } just runs

                    mockMvc
                        .post("/v1/tech-stack") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(stackInsertRequest)
                        }.andExpect {
                            status { isCreated() }
                            jsonPath("$.success") { value(true) }
                        }
                }
            }

            context("이름과 URL을 보내면") {
                it("단일 스택 저장에 성공하고, status 200을 응답한다.") {
                    val stackInsertRequest =
                        StackInsertRequest(
                            name = "Kotlin",
                            imageUrl = "https://avatars.githubusercontent.com/u/102505374?v=4",
                        )

                    every { stackService.insertStack(any(StackInsertRequest::class)) } just runs

                    mockMvc
                        .post("/v1/tech-stack") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(stackInsertRequest)
                        }.andExpect {
                            status { isCreated() }
                            jsonPath("$.success") { value(true) }
                        }
                }
            }

            context("이름이 없으면") {
                it("단일 스택 저장에 실패하고, status 400을 응답한다.") {
                    val stackInsertRequest = StackInsertRequest(name = "", imageUrl = null)

                    verify { stackService wasNot called }

                    mockMvc
                        .post("/v1/tech-stack") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(stackInsertRequest)
                        }.andExpect {
                            status { isBadRequest() }
                            jsonPath("$.success") { value(false) }
                            jsonPath("$.code") { value(ErrorCode.METHOD_ARGUMENT_NOT_VALID.code) }
                        }
                }
            }

            context("URL 형식이 올바르지 않으면") {
                it("단일 스택 저장에 실패하고, status 400을 응답한다.") {
                    val stackInsertRequest = StackInsertRequest(name = "Kotlin", imageUrl = "data://image")

                    verify { stackService wasNot called }

                    mockMvc
                        .post("/v1/tech-stack") {
                            contentType = MediaType.APPLICATION_JSON
                            content = objectMapper.writeValueAsString(stackInsertRequest)
                        }.andExpect {
                            status { isBadRequest() }
                            jsonPath("$.success") { value(false) }
                            jsonPath("$.code") { value(ErrorCode.METHOD_ARGUMENT_NOT_VALID.code) }
                        }
                }
            }
        }

        describe("POST /tech-stack/upload-csv - CSV를 이용한 스택 삽입 시") {
            context("form-data에 key를 file해서 stack.csv를 넣으면") {
                it("CSV 스택 저장에 성공하고, status 201을 응답한다.") {
                    val mockFile = StackFixture.createMockMultipartFile()

                    every { stackService.bulkInsertFromCSV(any()) } returns 3

                    mockMvc
                        .multipart("/v1/tech-stack/upload-csv") {
                            file(mockFile)
                        }.andExpect {
                            status { isCreated() }
                            jsonPath("$.success") { value(true) }
                        }
                }
            }
        }

        describe("GET /tech-stack/autocomplete - 스택 자동 완성 검색 시") {
            context("String으로 된 쿼리를 보내면") {
                it("status 200과 자동 완성 결과를 응답한다.") {
                    val query = "Rea"
                    val suggestions = listOf("React", "React Native", "PrimeReact")

                    every { stackService.getSuggestions(query) } returns suggestions

                    mockMvc
                        .get("/v1/tech-stack/autocomplete") {
                            param("query", query)
                        }.andExpect {
                            status { isOk() }
                            jsonPath("$.success") { value(true) }
                            jsonPath("$.data[0]") { value(suggestions[0]) }
                            jsonPath("$.data[1]") { value(suggestions[1]) }
                            jsonPath("$.data[2]") { value(suggestions[2]) }
                        }
                }
            }
        }
    })
