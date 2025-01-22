package org.pofo.api.domain.security.token

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class TokenRepositoryConfig {
    @Bean
    @Profile("local")
    fun inMemoryBannedAccessTokenRepository(): TokenRepository<BannedAccessToken> = InMemoryTokenRepository()

    @Bean
    @Profile("!local")
    fun redisBannedAccessTokenRepository(
        redisTemplate: RedisTemplate<String, String>,
    ): TokenRepository<BannedAccessToken> = RedisTokenRepository(redisTemplate)

    @Bean
    @Profile("local")
    fun inMemoryRefreshTokenRepository(): TokenRepository<RefreshToken> = InMemoryTokenRepository()

    @Bean
    @Profile("!local")
    fun redisRefreshTokenRepository(redisTemplate: RedisTemplate<String, String>): TokenRepository<RefreshToken> =
        RedisTokenRepository(redisTemplate)
}
