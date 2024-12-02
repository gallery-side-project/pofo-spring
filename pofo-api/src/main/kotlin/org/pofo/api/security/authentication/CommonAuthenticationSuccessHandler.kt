package org.pofo.api.security.authentication

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.pofo.api.common.response.ApiResponse
import org.pofo.api.security.PrincipalDetails
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class CommonAuthenticationSuccessHandler : AuthenticationSuccessHandler {
    private val objectMapper = jacksonObjectMapper()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val principal = authentication.principal as PrincipalDetails
        val user = principal.user
        logger.info { "login success: email: ${user.email}, role: ${user.role}" }

        response.apply {
            this.status = HttpStatus.OK.value()
            this.contentType = MediaType.APPLICATION_JSON_VALUE
        }

        objectMapper.writeValue(
            response.writer,
            ApiResponse.success(user),
        )
    }
}
