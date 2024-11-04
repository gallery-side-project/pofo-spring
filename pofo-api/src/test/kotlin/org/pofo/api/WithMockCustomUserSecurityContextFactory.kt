package org.pofo.api

import org.pofo.api.security.local.LocalAuthenticationToken
import org.pofo.domain.user.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

internal class WithMockCustomUserSecurityContextFactory : WithSecurityContextFactory<WithMockCustomUser> {
    override fun createSecurityContext(customUser: WithMockCustomUser): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()
        val user = User.create(customUser.email, customUser.password)
        val token =
            LocalAuthenticationToken(
                principal = user,
                authorities =
                    listOf(
                        SimpleGrantedAuthority(user.role.name),
                    ),
            )
        context.authentication = token
        return context
    }
}
