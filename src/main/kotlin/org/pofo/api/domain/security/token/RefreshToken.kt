package org.pofo.api.domain.security.token

class RefreshToken(
    userId: Long,
    value: String,
    expiration: Long,
) : Token(userId, value, expiration) {
    companion object {
        const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    override fun getKey(): String = "${REFRESH_TOKEN_KEY}:$userId"
}
