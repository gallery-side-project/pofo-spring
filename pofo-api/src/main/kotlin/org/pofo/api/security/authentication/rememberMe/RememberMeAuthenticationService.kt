package org.pofo.api.security.authentication.rememberMe

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.pofo.api.security.PrincipalDetails
import org.pofo.domain.domain.security.SessionPersistent
import org.pofo.domain.domain.security.SessionPersistentRepository
import org.pofo.domain.domain.user.UserRepository
import org.springframework.security.authentication.RememberMeAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.authentication.rememberme.CookieTheftException
import org.springframework.security.web.authentication.rememberme.InvalidCookieException
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException
import java.security.SecureRandom
import java.util.Base64

private val logger = KotlinLogging.logger {}

class RememberMeAuthenticationService(
    private val key: String,
    private val cookieName: String,
    private val sessionPersistentRepository: SessionPersistentRepository,
    private val userRepository: UserRepository,
) : RememberMeServices {
    private val secureRandom = SecureRandom()
    private val cookiePath: String = "/"
    private val cookieDomain: String? = null
    private val useSecureCookie: Boolean = true
    private val useHttpOnlyCookie: Boolean = true
    private val delimiter: String = ":"
    private val seriesLength: Int = 32
    private val secretLength: Int = 32
    private val alwaysRemember: Boolean = false
    private val parameter: String = "remember-me"
    private val tokenValidityInSeconds: Int = 60 * 60 * 24 * 15

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
            val persistentData = this.decodeCookie(cookieValue)
            val principal = this.processAutoLoginCookie(persistentData, response)
            logger.debug { "Remember-me cookie accepted: ${principal.user.email}" }
            return RememberMeAuthenticationToken(
                this.key,
                principal,
                principal.authorities,
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
        val persistentData = cookieAsPlainText.split(delimiter)
        return persistentData
    }

    private fun encodeCookie(persistentData: List<String>): Cookie {
        val series = persistentData[0]
        val secret = persistentData[1]

        val cookieValueAsPlainText = "$series$delimiter$secret"
        return Cookie(
            this.cookieName,
            Base64.getEncoder().withoutPadding().encodeToString(cookieValueAsPlainText.toByteArray()),
        )
    }

    /**
     * remember-me 쿠키를 추가합니다.
     */
    private fun addRememberMeCookie(
        persistentData: List<String>,
        response: HttpServletResponse,
    ) {
        val cookie =
            this.encodeCookie(persistentData).apply {
                maxAge = this@RememberMeAuthenticationService.tokenValidityInSeconds
                path = this@RememberMeAuthenticationService.cookiePath
                if (this@RememberMeAuthenticationService.cookieDomain != null) {
                    domain = this@RememberMeAuthenticationService.cookieDomain
                }
                isHttpOnly = this@RememberMeAuthenticationService.useHttpOnlyCookie
                secure = this@RememberMeAuthenticationService.useSecureCookie
            }
        response.addCookie(cookie)
    }

    /**
     * remember-me 쿠키의 값을 가져옵니다.
     */
    private fun extractRememberMeCookie(request: HttpServletRequest): String? {
        val cookies = request.cookies
        if (cookies != null && cookies.isNotEmpty()) {
            for (cookie in cookies) {
                if (cookie.name == this.cookieName) {
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
        val cookie = Cookie(this.cookieName, null)
        cookie.maxAge = 0
        response.addCookie(cookie)
    }

    /**
     * 쿠키의 값을 검증하고, 데이터베이스에 저장한 뒤 유저를 가져옵니다.
     */
    private fun processAutoLoginCookie(
        persistentData: List<String>,
        response: HttpServletResponse,
    ): PrincipalDetails {
        if (persistentData.size != 2) {
            throw InvalidCookieException("Remember-me cookie does not have 2 values")
        }
        val presentedSeries = persistentData[0]
        val presentedValue = persistentData[1]
        val token =
            this.sessionPersistentRepository.findBySeries(presentedSeries)
                ?: throw RememberMeAuthenticationException("No remember-me token found for series id: $presentedSeries")

        /**
         * 토큰 값 체크
         */
        if (presentedValue != token.secret) {
            this.sessionPersistentRepository.removeByEmail(token.email)
            throw CookieTheftException(
                "Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack.",
            )
        }

        // 토큰 만료 확인
        if (token.lastUsedAt.time + this.tokenValidityInSeconds.toLong() * 1000L <
            System.currentTimeMillis()
        ) {
            throw RememberMeAuthenticationException("Remember-me token has expired")
        }

        logger.debug { "Refreshing remember-me token for user: ${token.email}, series: ${token.series}" }
        val newToken = token.updateValue(this.generateSecret())

        val user =
            try {
                this.sessionPersistentRepository.updateValueBySeries(
                    newToken.series,
                    newToken.secret,
                    newToken.lastUsedAt,
                )
                this.addRememberMeCookie(listOf(newToken.series, newToken.secret), response)
                this.userRepository.findByEmail(token.email)
            } catch (ex: Exception) {
                logger.error(ex) { "Failed to update remember-me token" }
                throw RememberMeAuthenticationException("AutoLogin failed due to data access problem")
            }
        return PrincipalDetails(user)
    }

    private fun generateSerie(): String {
        val randomBytes = ByteArray(seriesLength)
        secureRandom.nextBytes(randomBytes)
        return Base64.getEncoder().withoutPadding().encodeToString(randomBytes)
    }

    private fun generateSecret(): String {
        val randomBytes = ByteArray(secretLength)
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
        if (!this.rememberMeRequested(request)) {
            logger.debug { "Remember-me login not requested." }
        } else {
            this.onLoginSuccess(response, successfulAuthentication)
        }
    }

    private fun rememberMeRequested(request: HttpServletRequest): Boolean {
        if (this.alwaysRemember) {
            return true
        } else {
            val paramValue = request.getParameter(this.parameter)
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
                    "Did not send remember-me cookie (principal did not set parameter: ${this.parameter})"
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
        val principal = successfulAuthentication.principal as PrincipalDetails
        val user = principal.user
        logger.debug { "Creating new remember-me token for user: ${user.email}" }
        val sessionPersistent =
            SessionPersistent.create(user.email, this.generateSerie(), this.generateSecret())

        try {
            this.sessionPersistentRepository.save(sessionPersistent)
            this.addRememberMeCookie(
                listOf(sessionPersistent.series, sessionPersistent.secret),
                response,
            )
        } catch (ex: Exception) {
            logger.error(ex) { "Failed to save remember-me token" }
        }
    }
}
