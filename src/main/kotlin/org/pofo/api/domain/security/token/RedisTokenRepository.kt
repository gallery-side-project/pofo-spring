package org.pofo.api.domain.security.token

import org.springframework.data.redis.core.StringRedisTemplate
import java.time.Duration

class RedisTokenRepository<T : Token>(
    private val redisTemplate: StringRedisTemplate,
    private val key: String,
) : TokenRepository<T> {
    override fun save(token: T) {
        redisTemplate.opsForValue().set(
            "$key:${token.userId}",
            token.value,
            Duration.ofSeconds(token.expiration / 1000),
        )
    }

    override fun findByUserIdOrNull(userId: Long): String? = redisTemplate.opsForValue().get("$key:$userId")

    override fun deleteByUserId(userId: Long) {
        redisTemplate.delete("$key:$userId")
    }
}
