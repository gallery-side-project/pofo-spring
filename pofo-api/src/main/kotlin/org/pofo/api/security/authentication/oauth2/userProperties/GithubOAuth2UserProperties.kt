package org.pofo.api.security.authentication.oauth2.userProperties

class GithubOAuth2UserProperties(attributes: Map<String, Any>) : OAuth2UserProperties(
    id = attributes["id"].toString(),
    imageUrl = attributes["avatar_url"].toString(),
    attributes = attributes,
)
