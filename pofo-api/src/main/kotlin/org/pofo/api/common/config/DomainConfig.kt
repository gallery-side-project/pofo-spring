package org.pofo.api.common.config

import org.pofo.domain.rds.config.JpaConfig
import org.pofo.domain.rds.config.QueryDslConfig
import org.pofo.domain.redis.config.RedisConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(JpaConfig::class, QueryDslConfig::class, RedisConfig::class)
class DomainConfig
