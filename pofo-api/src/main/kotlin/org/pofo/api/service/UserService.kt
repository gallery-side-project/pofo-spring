package org.pofo.api.service

import org.pofo.api.dto.RegisterRequest
import org.pofo.common.exception.CustomException
import org.pofo.common.exception.ErrorCode
import org.pofo.domain.domain.user.User
import org.pofo.domain.domain.user.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun createUser(registerRequest: RegisterRequest): User {
        val email = registerRequest.email
        if (userRepository.existsByEmail(email)) {
            throw CustomException(ErrorCode.USER_EXISTS)
        }
        val encodedPassword = passwordEncoder.encode(registerRequest.password)
        val user = User.create(email, encodedPassword)
        return userRepository.save(user)
    }

    fun fetchUserByEmail(email: String): User {
        val user = userRepository.findByEmail(email) ?: throw CustomException(ErrorCode.USER_NOT_FOUND)
        return user
    }
}
