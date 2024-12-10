package org.pofo.api.security.oauth2.userProperties

class GithubOAuth2UserProperties(attributes: Map<String, Any>) : OAuth2UserProperties(
    attributes = attributes,
    id = attributes["id"].toString(),
    email = attributes["email"].toString(),
    imageUrl = attributes["avatar_url"].toString(),
)
