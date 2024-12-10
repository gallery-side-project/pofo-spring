package org.pofo.api.security.jwt

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.pofo.api.security.PrincipalDetails
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

private val logger = KotlinLogging.logger {}

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessTokenInHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (accessTokenInHeader != null && accessTokenInHeader.startsWith("Bearer ")) {
            val accessToken = accessTokenInHeader.substring(7)
            val jwtTokenData = jwtService.extractTokenData(accessToken)
            logger.debug { "Access token detected: $accessToken" }

            val principal = PrincipalDetails(jwtTokenData)
            val token = UsernamePasswordAuthenticationToken(principal, "", principal.authorities)
            SecurityContextHolder.getContext().authentication = token
        }
        filterChain.doFilter(request, response)
    }
}
