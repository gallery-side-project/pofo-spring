package org.pofo.api.domain.security.token

class BannedAccessToken(
    userId: Long,
    value: String,
    expiration: Long,
) : Token(userId, value, expiration) {
    companion object {
        const val BANNED_ACCESS_TOKEN_KEY = "banned_access_token"
    }

    override fun getKey(): String = "${BANNED_ACCESS_TOKEN_KEY}:$userId"
}
