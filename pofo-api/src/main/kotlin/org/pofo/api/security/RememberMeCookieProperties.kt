package org.pofo.api.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "session.remember-me-cookie")
data class RememberMeCookieProperties(
    var name: String,
    var secure: Boolean,
    var httpOnly: Boolean,
    var path: String,
    var tokenValidityInSeconds: Int,
    var domain: String? = null,
)
