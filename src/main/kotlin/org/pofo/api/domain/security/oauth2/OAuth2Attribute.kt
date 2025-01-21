package org.pofo.api.domain.security.oauth2

import org.pofo.api.domain.security.oauth2.userProperties.GithubOAuth2UserProperties
import org.pofo.api.domain.security.oauth2.userProperties.OAuth2UserProperties
import org.pofo.api.domain.user.UserSocialType

class OAuth2Attribute private constructor(
    val nameAttributeKey: String,
    val userProperties: OAuth2UserProperties,
) {
    companion object {
        fun of(
            socialType: UserSocialType,
            nameAttributeKey: String,
            attributes: Map<String, Any>,
        ): OAuth2Attribute =
            when (socialType) {
                UserSocialType.GITHUB ->
                    ofGithub(
                        nameAttributeKey,
                        attributes,
                    )
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
