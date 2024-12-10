package org.pofo.api.service

import org.pofo.api.dto.LoginRequest
import org.pofo.api.dto.RegisterRequest
import org.pofo.api.dto.TokenResponse
import org.pofo.api.security.jwt.JwtService
import org.pofo.api.security.jwt.JwtTokenData
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
    private val jwtService: JwtService,
) {
    @Transactional
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

    fun login(loginRequest: LoginRequest): TokenResponse {
        val email = loginRequest.email
        val password = loginRequest.password

        val findUser = userRepository.findByEmail(email) ?: throw CustomException(ErrorCode.USER_LOGIN_FAILED)
        if (!passwordEncoder.matches(password, findUser.password)) {
            throw CustomException(ErrorCode.USER_LOGIN_FAILED)
        }

        val accessToken = jwtService.generateAccessToken(
            JwtTokenData(
                userId = findUser.id,
                email = findUser.email,
                name = "some name",
                role = findUser.role,
            )
        )
        val refreshToken = jwtService.generateRefreshToken(findUser.id)

        // TODO: refreshToken 저장
        return TokenResponse(accessToken, refreshToken)
    }

//    fun reIssueToken(): TokenResponse {}

//    fun logout() {}
}
