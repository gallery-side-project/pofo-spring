package org.pofo.api.security.local

import org.pofo.domain.user.User
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class LocalAuthenticationToken private constructor(
    private val principal: Any,
    private val credentials: Any,
    authorities: List<GrantedAuthority>,
) : AbstractAuthenticationToken(authorities) {
    /**
     * 인증되지 않은 Authentication 객체를 생성합니다.
     */
    constructor(email: String, password: String) : this(email, password, emptyList())

    /**
     * 인증된 Authentication 객체를 principal을 User로 설정하여 생성합니다.
     * @see org.springframework.security.core.AuthenticatedPrincipal
     */
    constructor(principal: User, authorities: List<GrantedAuthority>) : this(principal, "", authorities) {
        this.isAuthenticated = true
    }

    override fun getPrincipal(): Any {
        return principal
    }

    override fun getCredentials(): Any {
        return credentials
    }
}
