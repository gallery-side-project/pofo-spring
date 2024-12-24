package org.pofo.api.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.pofo.api.common.response.ApiResponse
import org.pofo.api.common.util.CookieUtil
import org.pofo.api.docs.UserApiDocs
import org.pofo.api.dto.LoginRequest
import org.pofo.api.dto.RegisterRequest
import org.pofo.api.dto.TokenResponse
import org.pofo.api.security.PrincipalDetails
import org.pofo.api.security.jwt.JwtService
import org.pofo.api.service.UserService
import org.pofo.common.exception.ErrorCode
import org.pofo.common.response.Version
import org.pofo.domain.rds.domain.user.User
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Version.V1 + "/user")
class UserController(
    private val userService: UserService,
    private val cookieUtil: CookieUtil,
) : UserApiDocs {
    companion object {
        const val REFRESH_COOKIE_NAME = "POFO_RTN"
    }

    @PostMapping("")
    override fun register(
        @RequestBody registerRequest: RegisterRequest,
    ): ApiResponse<User> {
        val user = userService.createUser(registerRequest)
        return ApiResponse.success(user)
    }

    @GetMapping("/me")
    override fun getMe(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
    ): ApiResponse<User> {
        val user = userService.getUserById(principalDetails.jwtTokenData.userId)
        return ApiResponse.success(user)
    }

    @PostMapping("/login")
    override fun login(
        response: HttpServletResponse,
        @RequestBody loginRequest: LoginRequest,
    ): ApiResponse<TokenResponse> {
        val tokenResponse = userService.login(loginRequest)

        createRefreshTokenCookie(response, tokenResponse)
        return ApiResponse.success(tokenResponse)
    }

    @PostMapping("/re-issue")
    override fun reIssue(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ApiResponse<*> {
        val refreshTokenCookie = cookieUtil.getCookieFromRequest(request, cookieName = REFRESH_COOKIE_NAME)
        if (refreshTokenCookie == null) {
            response.status = HttpStatus.BAD_REQUEST.value()
            return ApiResponse.failure(ErrorCode.USER_LOGIN_FAILED)
        }
        val refreshToken = refreshTokenCookie.value
        val tokenResponse = userService.reIssueToken(refreshToken)

        createRefreshTokenCookie(response, tokenResponse)
        return ApiResponse.success(tokenResponse)
    }

    private fun createRefreshTokenCookie(
        response: HttpServletResponse,
        tokenResponse: TokenResponse,
    ) {
        val cookie =
            cookieUtil.createCookie(
                cookieName = REFRESH_COOKIE_NAME,
                maxAge = JwtService.REFRESH_TOKEN_EXPIRATION / 1000,
                value = tokenResponse.refreshToken,
            )

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }

    @PostMapping("/logout")
    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
    ): ApiResponse<Unit> {
        val accessToken = getAccessToken(request)
        userService.logout(principalDetails.jwtTokenData.userId, accessToken)
        val deletingRefreshTokenCookie = cookieUtil.createDeletingCookie(REFRESH_COOKIE_NAME)

        response.setHeader(HttpHeaders.SET_COOKIE, deletingRefreshTokenCookie.toString())
        return ApiResponse.success(Unit)
    }

    private fun getAccessToken(request: HttpServletRequest): String =
        request.getHeader(HttpHeaders.AUTHORIZATION)!!.removePrefix("Bearer ")
}
