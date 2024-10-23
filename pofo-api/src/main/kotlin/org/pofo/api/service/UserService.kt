package org.pofo.api.service

import org.pofo.api.dto.RegisterRequest
import org.pofo.api.security.CustomUserDetails
import org.pofo.common.error.CustomError
import org.pofo.common.error.ErrorType
import org.pofo.domain.user.User
import org.pofo.domain.user.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {
    fun createUser(registerRequest: RegisterRequest): User {
        val email = registerRequest.email
        if (userRepository.existsByEmail(email)) {
            throw CustomError(ErrorType.USER_EXISTS)
        }
        val encodedPassword = passwordEncoder.encode(registerRequest.password)
        val user = User.create(email, encodedPassword)
        return userRepository.save(user)
    }

    fun fetchUserByEmail(email: String): User {
        val user = userRepository.findByEmail(email) ?: throw CustomError(ErrorType.USER_NOT_FOUND)
        return user
    }

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username) ?: throw CustomError(ErrorType.USER_NOT_FOUND)
        return CustomUserDetails(user)
    }
}
