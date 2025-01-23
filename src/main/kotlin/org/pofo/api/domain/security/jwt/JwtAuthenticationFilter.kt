package org.pofo.api.domain.security.jwt

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.pofo.api.domain.security.PrincipalDetails
import org.pofo.api.domain.security.token.BannedAccessToken
import org.pofo.api.domain.security.token.TokenRepository
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

private val log = KotlinLogging.logger {}

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val bannedAccessTokenTokenRepository: TokenRepository<BannedAccessToken>,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val accessTokenInHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (accessTokenInHeader != null && accessTokenInHeader.startsWith("Bearer ")) {
            val accessToken = accessTokenInHeader.substring(7)
            val jwtTokenData = jwtService.extractTokenData(accessToken)
            log.debug { "Access token detected: $accessToken" }

            val bannedAccessToken = bannedAccessTokenTokenRepository.findByUserIdOrNull(jwtTokenData.userId)

            if (bannedAccessToken != null && bannedAccessToken == accessToken) {
                log.debug { "Banned access token detected: $bannedAccessToken" }
                return filterChain.doFilter(request, response)
            }

            val principal = PrincipalDetails(jwtTokenData)
            val token = UsernamePasswordAuthenticationToken(principal, "", principal.authorities)
            SecurityContextHolder.getContext().authentication = token
        }
        filterChain.doFilter(request, response)
    }
}
