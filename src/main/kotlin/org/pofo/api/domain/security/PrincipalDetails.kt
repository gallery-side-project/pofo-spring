package org.pofo.api.domain.security

import org.pofo.api.domain.security.jwt.JwtTokenData
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class PrincipalDetails(
    val jwtTokenData: JwtTokenData,
) : UserDetails,
    OAuth2User {
    override fun getUsername(): String = jwtTokenData.email

    override fun getPassword(): String = ""

    override fun getName(): String = jwtTokenData.name

    override fun getAttributes(): Map<String, Any>? = null

    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority(jwtTokenData.role.name))
}
