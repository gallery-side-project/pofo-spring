package org.pofo.api.security.oauth2.userProperties

abstract class OAuth2UserProperties(
    protected val attributes: Map<String, Any>,
    val id: String,
    val email: String,
    val avatarUrl: String,
)
