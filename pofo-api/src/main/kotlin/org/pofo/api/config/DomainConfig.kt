package org.pofo.api.config

import org.pofo.domain.config.JpaConfig
import org.pofo.domain.config.QueryDslConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(JpaConfig::class, QueryDslConfig::class)
class DomainConfig
