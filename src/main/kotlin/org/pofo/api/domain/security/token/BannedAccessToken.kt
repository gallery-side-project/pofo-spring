package org.pofo.api.domain.security.token

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash("banned_access_token")
class BannedAccessToken(
    @Id
    val userId: Long,
    val value: String,
    @TimeToLive
    val expiration: Long,
)
