package org.pofo.api.security

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.pofo.domain.security.remember_me.CustomRememberMeToken
import org.pofo.domain.security.remember_me.CustomRememberMeTokenRepository
import org.pofo.domain.user.User
import org.pofo.domain.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.authentication.rememberme.CookieTheftException
import org.springframework.security.web.authentication.rememberme.InvalidCookieException
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException
import java.security.SecureRandom
import java.util.Base64
import java.util.Date

private val logger = KotlinLogging.logger {}

class CustomRememberMeService(
    private val seriesLength: Int = 32,
    private val tokenValueLength: Int = 32,
    private val alwaysRemember: Boolean = false,
    private val parameter: String = "remember-me",
) : RememberMeServices {
    private val delimiter = ":"
    private val secureRandom = SecureRandom()

    @Autowired
    private lateinit var rememberMeCookieProperties: RememberMeCookieProperties

    @Autowired
    private lateinit var tokenRepository: CustomRememberMeTokenRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    /**
     * 유저의 세션이 만료되었을 때 이 함수를 통해 세션을 재발급해줍니다.
     */
    override fun autoLogin(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): Authentication? {
        val cookieValue = this.extractRememberMeCookie(request) ?: return null
        logger.debug { "Remember-me cookie detected: $cookieValue" }

        if (cookieValue.isEmpty()) {
            logger.debug { "remember-me cookie is empty" }
            this.removeRememberMeCookie(response)
            return null
        }

        try {
            val tokenValues = this.decodeCookie(cookieValue)
            val user: User = this.processAutoLoginCookie(tokenValues, response)
            logger.debug { "Remember-me cookie accepted: ${user.email}" }
            return CustomAuthenticationToken(
                principal = user,
                authorities = listOf(SimpleGrantedAuthority(user.role.name)),
            )
        } catch (ex: RememberMeAuthenticationException) {
            logger.debug { ex.message }
        }

        this.removeRememberMeCookie(response)
        return null
    }

    private fun decodeCookie(cookieValue: String): List<String> {
        val cookieAsPlainText =
            try {
                String(Base64.getDecoder().decode(cookieValue.toByteArray()))
            } catch (ex: IllegalArgumentException) {
                throw InvalidCookieException(
                    "Remember-me cookie's values was not Base64 encoded; value was '$cookieValue'",
                )
            }
        val tokenValues = cookieAsPlainText.split(delimiter)
        return tokenValues
    }

    private fun encodeCookie(tokenValues: List<String>): Cookie {
        val series = tokenValues[0]
        val tokenValue = tokenValues[1]

        val cookieValueAsPlainText = "$series$delimiter$tokenValue"
        return Cookie(
            this.rememberMeCookieProperties.name,
            Base64.getEncoder().withoutPadding().encodeToString(cookieValueAsPlainText.toByteArray()),
        )
    }

    /**
     * remember-me 쿠키를 추가합니다.
     */
    private fun addRememberMeCookie(
        tokenValues: List<String>,
        response: HttpServletResponse,
    ) {
        val cookie =
            this.encodeCookie(tokenValues).apply {
                maxAge = rememberMeCookieProperties.tokenValidityInSeconds
                path = rememberMeCookieProperties.path
                if (rememberMeCookieProperties.domain != null) {
                    domain = rememberMeCookieProperties.domain
                }
                isHttpOnly = rememberMeCookieProperties.httpOnly
                secure = rememberMeCookieProperties.secure
            }
        response.addCookie(cookie)
    }

    /**
     * remember-me 쿠키의 value를 가져옵니다.
     */
    private fun extractRememberMeCookie(request: HttpServletRequest): String? {
        val cookies = request.cookies
        if (cookies != null && cookies.isNotEmpty()) {
            for (cookie in cookies) {
                if (cookie.name == this.rememberMeCookieProperties.name) {
                    return cookie.value
                }
            }
        }
        return null
    }

    /**
     * remember-me 쿠키를 삭제합니다.
     */
    private fun removeRememberMeCookie(response: HttpServletResponse) {
        logger.debug { "Remove remember-me cookie" }
        val cookie = Cookie(this.rememberMeCookieProperties.name, null)
        cookie.maxAge = 0
        response.addCookie(cookie)
    }

    /**
     * 쿠키의 값을 검증하고, 데이터베이스에 저장한 뒤 유저를 가져옵니다.
     */
    private fun processAutoLoginCookie(
        tokenValues: List<String>,
        response: HttpServletResponse,
    ): User {
        if (tokenValues.size != 2) {
            throw InvalidCookieException("Remember-me cookie does not have 2 values")
        }
        val presentedSeries = tokenValues[0]
        val presentedTokenValue = tokenValues[1]
        val token: CustomRememberMeToken =
            this.tokenRepository.findBySeries(presentedSeries)
                ?: throw RememberMeAuthenticationException("No remember-me token found for series id: $presentedSeries")

        /**
         * 토큰 값 체크
         */
        if (presentedTokenValue != token.tokenValue) {
            this.tokenRepository.removeByEmail(token.email)
            throw CookieTheftException(
                "Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack.",
            )
        }

        // 토큰 만료 확인
        if (token.lastUsedAt.time + this.rememberMeCookieProperties.tokenValidityInSeconds.toLong() * 1000L <
            System.currentTimeMillis()
        ) {
            throw RememberMeAuthenticationException("Remember-me token has expired")
        }

        logger.debug { "Refreshing remember-me token for user: ${token.email}, series: ${token.series}" }
        val newToken = token.updateTokenValue(this.generateTokenValue())

        val user =
            try {
                this.tokenRepository.updateToken(newToken.series, newToken.tokenValue, newToken.lastUsedAt)
                this.addRememberMeCookie(listOf(newToken.series, newToken.tokenValue), response)
                this.userRepository.findByEmail(token.email)
            } catch (ex: Exception) {
                logger.error(ex) { "Failed to update remember-me token" }
                throw RememberMeAuthenticationException("AutoLogin failed due to data access problem")
            }
        return user
    }

    private fun generateSerie(): String {
        val randomBytes = ByteArray(seriesLength)
        secureRandom.nextBytes(randomBytes)
        return Base64.getEncoder().withoutPadding().encodeToString(randomBytes)
    }

    private fun generateTokenValue(): String {
        val randomBytes = ByteArray(tokenValueLength)
        secureRandom.nextBytes(randomBytes)
        return Base64.getEncoder().withoutPadding().encodeToString(randomBytes)
    }

    /**
     * 필터에서 로그인 시도가 실패했을 때 호출됩니다.
     */
    override fun loginFail(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        this.removeRememberMeCookie(response)
    }

    override fun loginSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        successfulAuthentication: Authentication,
    ) {
        if (!this.rememberMeRequested(request, this.parameter)) {
            logger.debug { "Remember-me login not requested." }
        } else {
            this.onLoginSuccess(response, successfulAuthentication)
        }
    }

    private fun rememberMeRequested(
        request: HttpServletRequest,
        parameter: String?,
    ): Boolean {
        if (this.alwaysRemember) {
            return true
        } else {
            val paramValue = request.getParameter(parameter)
            if (paramValue == null ||
                !paramValue.equals("true", ignoreCase = true) &&
                !paramValue.equals(
                    "on",
                    ignoreCase = true,
                ) &&
                !paramValue.equals("yes", ignoreCase = true) &&
                paramValue != "1"
            ) {
                logger.debug {
                    "Did not send remember-me cookie (principal did not set parameter: $parameter)"
                }
                return false
            } else {
                return true
            }
        }
    }

    /**
     * 필터에서 로그인 시도가 성공했을 때 호출됩니다.
     */
    private fun onLoginSuccess(
        response: HttpServletResponse,
        successfulAuthentication: Authentication,
    ) {
        val user = successfulAuthentication.principal as User
        logger.debug { "Creating new remember-me token for user: ${user.email}" }
        val customRememberMeToken =
            CustomRememberMeToken.create(this.generateSerie(), user.email, this.generateTokenValue(), Date())

        try {
            this.tokenRepository.save(customRememberMeToken)
            this.addRememberMeCookie(
                listOf(customRememberMeToken.series, customRememberMeToken.tokenValue),
                response,
            )
        } catch (ex: Exception) {
            logger.error(ex) { "Failed to save remember-me token" }
        }
    }
}
