package org.pofo.api.security.authentication.oauth2.userProperties

abstract class OAuth2UserProperties(
    protected val attributes: Map<String, Any>,
    val id: String,
    val imageUrl: String,
)
