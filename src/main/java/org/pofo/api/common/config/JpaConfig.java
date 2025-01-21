package org.pofo.api.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"org.pofo.api.domain"})
@EnableJpaRepositories(basePackages = {"org.pofo.api.domain"})
public class JpaConfig {
}
