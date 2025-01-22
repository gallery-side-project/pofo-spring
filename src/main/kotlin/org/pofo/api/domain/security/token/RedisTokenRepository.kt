package org.pofo.api.domain.security.token

import org.springframework.data.redis.core.RedisTemplate

class RedisTokenRepository<T : Token>(
    private val redisTemplate: RedisTemplate<String, String>,
) : TokenRepository<T> {
    companion object {
        const val BANNED_ACCESS_TOKEN_KEY = "banned_access_token"
    }

    override fun save(token: T) {
        val fieldKey = "$BANNED_ACCESS_TOKEN_KEY:${token.userId}"
        redisTemplate.opsForValue().set(
            fieldKey,
            token.value,
            token.expiration / 1000,
        )
    }

    override fun findByUserIdOrNull(userId: Long): String? = redisTemplate.opsForValue().get(userId)

    override fun deleteByUserId(userId: Long) {
        val fieldKey = "$BANNED_ACCESS_TOKEN_KEY:$userId"
        redisTemplate.delete(fieldKey)
    }
}
