package org.pofo.api.security.oauth2

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationFailureHandler : SimpleUrlAuthenticationFailureHandler() {
    @Value("\${pofo.domain}")
    private lateinit var domain: String

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        redirectStrategy.sendRedirect(request, response, "$domain/login/callback?error=1")
    }
}
