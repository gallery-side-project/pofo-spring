package org.pofo.api.security.authentication.local

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class LocalAuthenticationFailureHandler : AuthenticationFailureHandler {
    private val objectMapper = jacksonObjectMapper()

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        logger.info { "login failed: ${exception.message}" }

        response.apply {
            this.status = HttpStatus.UNAUTHORIZED.value()
            this.contentType = MediaType.APPLICATION_JSON_VALUE
        }

        objectMapper.writeValue(
            response.writer,
            false,
        )
    }
}
