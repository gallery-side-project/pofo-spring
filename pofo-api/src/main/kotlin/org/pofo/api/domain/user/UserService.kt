package org.pofo.api.domain.user

import org.pofo.api.domain.security.jwt.JwtService
import org.pofo.api.domain.security.jwt.JwtTokenData
import org.pofo.api.domain.user.dto.TokenResponse
import org.pofo.api.domain.user.dto.UserLoginRequest
import org.pofo.api.domain.user.dto.UserRegisterRequest
import org.pofo.common.exception.CustomException
import org.pofo.common.exception.ErrorCode
import org.pofo.domain.rds.domain.user.User
import org.pofo.domain.rds.domain.user.UserRepository
import org.pofo.domain.redis.domain.accessToken.BannedAccessToken
import org.pofo.domain.redis.domain.accessToken.BannedAccessTokenRepository
import org.pofo.domain.redis.domain.refreshToken.RefreshToken
import org.pofo.domain.redis.domain.refreshToken.RefreshTokenRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(
    readOnly = true,
)
class UserService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val bannedAccessTokenRepository: BannedAccessTokenRepository,
) {
    @Transactional
    fun createUser(userRegisterRequest: UserRegisterRequest): User {
        val email = userRegisterRequest.email
        val username = userRegisterRequest.username
        if (userRepository
                .existsByEmailOrUsername(
                    email,
                    username,
                )
        ) {
            throw CustomException(
                ErrorCode.USER_EXISTS,
            )
        }

        val encodedPassword =
            passwordEncoder
                .encode(
                    userRegisterRequest.password,
                )

        val user =
            User
                .builder()
                .email(email)
                .password(encodedPassword)
                .username(userRegisterRequest.username)
                .build()

        return userRepository
            .save(
                user,
            )
    }

    fun getUserById(userId: Long): User =
        userRepository.findById(
            userId,
        )
            ?: throw CustomException(
                ErrorCode.USER_NOT_FOUND,
            )

    fun getUserByEmail(email: String): User =
        userRepository.findByEmail(
            email,
        )
            ?: throw CustomException(
                ErrorCode.USER_NOT_FOUND,
            )

    fun login(userLoginRequest: UserLoginRequest): TokenResponse {
        val email =
            userLoginRequest.email
        val password =
            userLoginRequest.password

        val findUser =
            userRepository.findByEmail(
                email,
            )
                ?: throw CustomException(
                    ErrorCode.USER_LOGIN_FAILED,
                )
        if (!passwordEncoder
                .matches(
                    password,
                    findUser.password,
                )
        ) {
            throw CustomException(
                ErrorCode.USER_LOGIN_FAILED,
            )
        }

        val tokenResponse =
            createTokenResponse(
                findUser,
            )
        val refreshTokenEntity =
            RefreshToken(
                findUser.id,
                tokenResponse.refreshToken,
                JwtService.REFRESH_TOKEN_EXPIRATION /
                    1000,
            )
        refreshTokenRepository
            .save(
                refreshTokenEntity,
            )
        return tokenResponse
    }

    fun reIssueToken(refreshToken: String): TokenResponse {
        val userId =
            jwtService
                .extractUserId(
                    refreshToken,
                )

        val refreshTokenEntity =
            refreshTokenRepository
                .findById(
                    userId,
                )
        if (refreshTokenEntity.isEmpty ||
            refreshToken !=
            refreshTokenEntity.get().value
        ) {
            throw CustomException(
                ErrorCode.USER_LOGIN_FAILED,
            )
        }

        val findUser =
            userRepository.findById(
                userId,
            )
                ?: throw CustomException(
                    ErrorCode.USER_LOGIN_FAILED,
                )
        val tokenResponse =
            createTokenResponse(
                findUser,
            )
        val newRefreshTokenEntity =
            RefreshToken(
                findUser.id,
                tokenResponse.refreshToken,
                JwtService.REFRESH_TOKEN_EXPIRATION /
                    1000,
            )
        refreshTokenRepository
            .save(
                newRefreshTokenEntity,
            )
        return tokenResponse
    }

    private fun createTokenResponse(user: User): TokenResponse {
        val accessToken =
            jwtService
                .generateAccessToken(
                    JwtTokenData(
                        userId = user.id,
                        email = user.email,
                        name = "some name",
                        role = user.role,
                    ),
                )
        val refreshToken =
            jwtService
                .generateRefreshToken(
                    user.id,
                )
        return TokenResponse(
            accessToken,
            refreshToken,
        )
    }

    fun logout(
        userId: Long,
        accessToken: String,
    ) {
        val bannedAccessToken =
            BannedAccessToken(
                userId,
                accessToken,
                JwtService.ACCESS_TOKEN_EXPIRATION /
                    1000,
            )
        bannedAccessTokenRepository
            .save(
                bannedAccessToken,
            )
        refreshTokenRepository
            .deleteById(
                userId,
            )
    }
}
