package org.pofo.api.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.pofo.api.common.response.ApiResponse
import org.pofo.api.dto.LoginRequest
import org.pofo.api.dto.RegisterRequest
import org.pofo.api.dto.TokenResponse
import org.pofo.api.security.PrincipalDetails
import org.pofo.domain.rds.domain.user.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerResponse

@Tag(name = "[User API]", description = "유저 관련 API")
interface UserApiDocs {
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(
        value = [
            SwaggerResponse(
                responseCode = "200",
                description = "회원가입 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "검증 성공 - 기존에 등록한 소셜 계정 없음",
                                value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "id": 0,
                                        "email": "string"
                                      }
                                    }
                                """,
                            ),
                            ExampleObject(
                                name = "검증 성공 - 기존에 등록한 소셜 계정 없음 (스웨거 문서 제작 예시로 임시로 중복해서 넣었습니다)",
                                value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "id": 0,
                                        "email": "string"
                                      }
                                    }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun register(
        @RequestBody registerRequest: RegisterRequest,
    ): ApiResponse<User>

    @Operation(summary = "내 정보 조회", description = "토큰을 활용하여 현재 로그인된 사용자의 정보를 조회합니다.")
    @ApiResponses(
        value = [
            SwaggerResponse(
                responseCode = "200",
                description = "정보 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "정보 조회 성공",
                                value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "id": 0,
                                        "email": "string"
                                      }
                                    }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun getMe(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
    ): ApiResponse<User>

    @Operation(summary = "로그인", description = "사용자 로그인 처리 및 토큰 발급합니다.")
    @ApiResponses(
        value = [
            SwaggerResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "로그인 성공",
                                value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "accessToken": "string"
                                      }
                                    }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun login(
        @Parameter(hidden = true) response: HttpServletResponse,
        @RequestBody loginRequest: LoginRequest,
    ): ApiResponse<TokenResponse>

    // TODO : 여기 예시좀 적어주세요. 와일드카드 Response라 Swagger에 안잡혀요.
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 사용하여 새 액세스 토큰을 발급합니다.")
    @ApiResponses(
        value = [
            SwaggerResponse(
                responseCode = "200",
                description = "토큰 재발급 성공",
            ),
        ],
    )
    fun reIssue(
        @Parameter(hidden = true) request: HttpServletRequest,
        @Parameter(hidden = true) response: HttpServletResponse,
    ): ApiResponse<*>

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃하고 리프레시 토큰 쿠키를 제거합니다.")
    @ApiResponses(
        value = [
            SwaggerResponse(
                responseCode = "200",
                description = "로그아웃 성공",
            ),
        ],
    )
    fun logout(
        @Parameter(hidden = true) request: HttpServletRequest,
        @Parameter(hidden = true) response: HttpServletResponse,
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
    ): ApiResponse<Unit>
}
