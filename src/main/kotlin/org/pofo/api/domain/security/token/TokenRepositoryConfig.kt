package org.pofo.api.domain.security.token

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.StringRedisTemplate

@Configuration
class TokenRepositoryConfig {
    companion object {
        const val BANNED_ACCESS_TOKEN_KEY = "banned_access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    @Bean
    @Profile("local")
    fun inMemoryBannedAccessTokenRepository(): TokenRepository<BannedAccessToken> = InMemoryTokenRepository()

    @Bean
    @Profile("!local")
    fun redisBannedAccessTokenRepository(template: StringRedisTemplate): TokenRepository<BannedAccessToken> =
        RedisTokenRepository(template, BANNED_ACCESS_TOKEN_KEY)

    @Bean
    @Profile("local")
    fun inMemoryRefreshTokenRepository(): TokenRepository<RefreshToken> = InMemoryTokenRepository()

    @Bean
    @Profile("!local")
    fun redisRefreshTokenRepository(template: StringRedisTemplate): TokenRepository<RefreshToken> =
        RedisTokenRepository(template, REFRESH_TOKEN_KEY)
}
