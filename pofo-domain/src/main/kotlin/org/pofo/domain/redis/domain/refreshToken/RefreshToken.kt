package org.pofo.domain.redis.domain.refreshToken

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash("refresh_token")
class RefreshToken(
    @Id
    val userId: Long,

    val value: String,

    @TimeToLive
    val expiration: Long
)
