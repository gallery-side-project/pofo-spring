package org.pofo.api.domain.security.oauth2

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.pofo.api.common.util.CookieUtil
import org.pofo.api.domain.security.PrincipalDetails
import org.pofo.api.domain.security.jwt.JwtService
import org.pofo.api.domain.user.UserController
import org.pofo.domain.redis.domain.refreshToken.RefreshToken
import org.pofo.domain.redis.domain.refreshToken.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val cookieUtil: CookieUtil,
    private val environment: Environment,
) : SimpleUrlAuthenticationSuccessHandler() {
    @Value("\${pofo.domain}")
    private lateinit var domain: String

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val principalDetails = authentication.principal as PrincipalDetails
        val jwtTokenData = principalDetails.jwtTokenData
        val accessToken = jwtService.generateAccessToken(jwtTokenData)
        val refreshToken = jwtService.generateRefreshToken(jwtTokenData.userId)
        val refreshTokenEntity =
            RefreshToken(jwtTokenData.userId, refreshToken, JwtService.REFRESH_TOKEN_EXPIRATION / 1000)
        refreshTokenRepository.save(refreshTokenEntity)

        val refreshTokenCookie =
            cookieUtil.createCookie(
                UserController.REFRESH_COOKIE_NAME,
                refreshToken,
                environment.matchesProfiles("prod"),
                JwtService.REFRESH_TOKEN_EXPIRATION,
            )
        response.setHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
        redirectStrategy.sendRedirect(request, response, "$domain/login/callback?access_token=$accessToken")
    }
}
