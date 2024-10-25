package org.pofo.api

import org.pofo.api.security.CustomAuthenticationToken
import org.pofo.domain.user.User
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

internal class WithMockCustomUserSecurityContextFactory : WithSecurityContextFactory<WithMockCustomUser> {
    override fun createSecurityContext(customUser: WithMockCustomUser): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()
        val principal = User.create(customUser.email, customUser.password)
        val authorities =
            listOf(
                SimpleGrantedAuthority(principal.role.name),
            )
        val auth: Authentication =
            CustomAuthenticationToken(principal, "", authorities)
        context.authentication = auth
        return context
    }
}
