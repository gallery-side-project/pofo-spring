package org.pofo.api.security.authentication.rememberMe

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "server.servlet.session.remember-me")
data class RememberMeCookieProperties(
    var name: String,
    var key: String,
)
