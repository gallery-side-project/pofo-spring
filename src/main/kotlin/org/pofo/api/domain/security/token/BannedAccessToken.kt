package org.pofo.api.domain.security.token

class BannedAccessToken(
    userId: Long,
    value: String,
    expiration: Long,
) : Token(userId, value, expiration)
