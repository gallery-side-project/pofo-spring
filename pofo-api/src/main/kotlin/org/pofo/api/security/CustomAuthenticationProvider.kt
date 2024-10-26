package org.pofo.api.security

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pofo.api.service.UserService
import org.pofo.common.error.CustomError
import org.pofo.common.error.ErrorType
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class CustomAuthenticationProvider(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        if (authentication.isAuthenticated) {
            return authentication
        }

        val email = authentication.name
        val password = authentication.credentials.toString()
        logger.debug { "login attempt for $email" }

        try {
            val user = userService.fetchUserByEmail(email)
            if (!passwordEncoder.matches(password, user.password)) {
                throw CustomError(ErrorType.INVALID_PASSWORD)
            }
            val token =
                CustomAuthenticationToken(
                    principal = user,
                    authorities = listOf(
                        SimpleGrantedAuthority(user.role.name),
                    ),
                )
            return token
        } catch (err: CustomError) {
            throw BadCredentialsException(err.message)
        }
    }

    override fun supports(authentication: Class<*>): Boolean =
        CustomAuthenticationToken::class.java.isAssignableFrom(authentication)
}
