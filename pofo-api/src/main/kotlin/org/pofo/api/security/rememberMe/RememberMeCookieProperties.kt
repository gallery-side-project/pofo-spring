package org.pofo.api.security.rememberMe

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "server.servlet.session.remember-me")
data class RememberMeCookieProperties(
    var name: String,
    var tokenKey: String,
)
