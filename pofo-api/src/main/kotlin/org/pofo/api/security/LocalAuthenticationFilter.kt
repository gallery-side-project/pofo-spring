package org.pofo.api.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.pofo.api.dto.LoginRequest
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher

class LocalAuthenticationFilter(
    requestMatcher: RequestMatcher,
) : AbstractAuthenticationProcessingFilter(requestMatcher) {
    private val objectMapper = jacksonObjectMapper()

    override fun attemptAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): Authentication {
        val loginRequest = objectMapper.readValue(request.reader, LoginRequest::class.java)

        val authenticationToken =
            LocalAuthenticationToken(
                email = loginRequest.email,
                password = loginRequest.password,
            )

        return this.authenticationManager.authenticate(authenticationToken)
    }
}
