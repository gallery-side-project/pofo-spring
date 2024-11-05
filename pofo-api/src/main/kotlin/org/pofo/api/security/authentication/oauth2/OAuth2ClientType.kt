package org.pofo.api.security.authentication.oauth2

enum class OAuth2ClientType {
    GITHUB,
    ;

    companion object {
        fun getClientType(registrationId: String): OAuth2ClientType {
            when (registrationId) {
                "github" -> return GITHUB
            }
            throw IllegalArgumentException("Unknown registration Id: $registrationId")
        }
    }
}
