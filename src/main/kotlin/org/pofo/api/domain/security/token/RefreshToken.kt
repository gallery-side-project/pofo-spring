package org.pofo.api.domain.security.token

class RefreshToken(
    userId: Long,
    value: String,
    expiration: Long,
) : Token(userId, value, expiration)
