package org.pofo.api.security.authentication.oauth2

import org.pofo.api.security.authentication.oauth2.userProperties.GithubOAuth2UserProperties
import org.pofo.api.security.authentication.oauth2.userProperties.OAuth2UserProperties

data class OAuth2Attribute private constructor(
    val nameAttributeKey: String,
    val oAuth2UserProperties: OAuth2UserProperties,
) {
    companion object {
        fun of(
            clientType: OAuth2ClientType,
            nameAttributeKey: String,
            attributes: Map<String, Any>,
        ): OAuth2Attribute =
            when (clientType) {
                OAuth2ClientType.GITHUB -> ofGithub(nameAttributeKey, attributes)
            }


        private fun ofGithub(
            nameAttributeKey: String,
            attributes: Map<String, Any>,
        ): OAuth2Attribute =
            OAuth2Attribute(
                nameAttributeKey,
                GithubOAuth2UserProperties(attributes),
            )
    }
}
