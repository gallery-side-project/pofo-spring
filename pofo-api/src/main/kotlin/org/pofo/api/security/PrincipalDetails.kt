package org.pofo.api.security

import org.pofo.domain.domain.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class PrincipalDetails(
    val user: User,
    private val attributes: Map<String, Any>? = null,
) : UserDetails,
    OAuth2User {
    private val authorities: Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority(user.role.name))

    override fun getUsername(): String = user.email

    override fun getPassword(): String = user.password

    override fun getName() = null

    override fun getAttributes(): Map<String, Any>? = attributes

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities
}
