package org.pofo.api

import org.pofo.api.security.PrincipalDetails
import org.pofo.api.security.jwt.JwtTokenData
import org.pofo.domain.domain.user.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

internal class WithMockCustomUserSecurityContextFactory() :
    WithSecurityContextFactory<WithMockCustomUser> {
    override fun createSecurityContext(customUser: WithMockCustomUser): SecurityContext {
        val user = User.create(customUser.email, customUser.password)
        val context = SecurityContextHolder.createEmptyContext()
        val authorities = listOf(SimpleGrantedAuthority(user.role.name))

        val principal = PrincipalDetails(JwtTokenData(
            userId = user.id,
            email = user.email,
            name = "some name",
            role = user.role,
        ))
        val token =
            UsernamePasswordAuthenticationToken.authenticated(
                principal,
                "",
                authorities,
            )
        context.authentication = token
        return context
    }
}
