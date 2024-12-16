package org.pofo.domain.rds.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"org.pofo.domain.rds.domain"})
@EnableJpaRepositories(basePackages = {"org.pofo.domain.rds.domain"})
public class JpaConfig {
}
