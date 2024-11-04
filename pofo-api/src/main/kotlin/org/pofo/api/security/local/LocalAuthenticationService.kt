package org.pofo.api.security.local

import org.pofo.api.security.PrincipalDetails
import org.pofo.domain.user.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class LocalAuthenticationService(
    private val userRepository: UserRepository,
): UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails? {
        val fetchUser = userRepository.findByEmail(email) ?: return null
        return PrincipalDetails(fetchUser)
    }
}
