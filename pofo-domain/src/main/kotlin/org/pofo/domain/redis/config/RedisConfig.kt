package org.pofo.domain.redis.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@Configuration
@EnableRedisRepositories(basePackages = ["org.pofo.domain.redis.domain"])
class RedisConfig
