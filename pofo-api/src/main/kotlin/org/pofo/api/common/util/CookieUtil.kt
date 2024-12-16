package org.pofo.api.common.util

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class CookieUtil {
    /**
     * request에서 cookieName에 해당하는 쿠키를 찾아서 반환합니다.
     *
     * @param request HttpServletRequest    쿠키를 찾을 request
     * @param cookieName String             찾을 쿠키의 이름
     * @return Cookie?                      쿠키가 존재하면 해당 쿠키를, 존재하지 않으면 null 반환합니다.
     */
    fun getCookieFromRequest(
        request: HttpServletRequest,
        cookieName: String,
    ): Cookie? =
        request.cookies
            .asSequence()
            .filter { cookieName == it.name }
            .firstOrNull()

    /**
     * cookieName에 해당하는 쿠키를 생성합니다.
     *
     * @param cookieName String 생성할 쿠키의 이름
     * @param value      String 생성할 쿠키의 값
     * @param maxAge     Long   생성할 쿠키의 만료 시간
     * @return ResponseCookie   생성된 쿠키
     */
    fun createCookie(
        cookieName: String,
        value: String,
        maxAge: Long,
    ): ResponseCookie =
        ResponseCookie
            .from(cookieName, value)
            .path("/")
            .httpOnly(true)
            .secure(true)
            .maxAge(maxAge)
            .build()

    /**
     * cookieName에 해당하는 쿠키를 제거합니다.
     *
     * @return ResponseCookie   쿠키의 maxAge가 0인 쿠키
     */
    fun createDeletingCookie(cookieName: String): ResponseCookie = createCookie(cookieName, "", 0)
}
