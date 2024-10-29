package org.pofo.api.security

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pofo.domain.user.UserRepository
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder

private val logger = KotlinLogging.logger {}

class LocalAuthenticationProvider(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication? {
        val email = authentication.name
        val password = authentication.credentials.toString()
        logger.debug { "login attempt for $email" }

        val user = userRepository.findByEmail(email) ?: return null
        if (!passwordEncoder.matches(password, user.password)) {
            return null
        }
        val token =
            LocalAuthenticationToken(
                principal = user,
                authorities =
                    listOf(
                        SimpleGrantedAuthority(user.role.name),
                    ),
            )
        return token
    }

    override fun supports(authentication: Class<*>): Boolean =
        LocalAuthenticationToken::class.java.isAssignableFrom(authentication)
}
