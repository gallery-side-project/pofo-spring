package org.pofo.api.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@Configuration
@EnableRedisRepositories(basePackages = ["org.pofo.api.domain.security.token"])
class RedisConfig
