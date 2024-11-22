package org.pofo.api.config

import org.pofo.infra.importer.EnablePofoInfraConfig
import org.pofo.infra.importer.PofoInfraConfigGroup
import org.springframework.context.annotation.Configuration

@Configuration
@EnablePofoInfraConfig(value = [PofoInfraConfigGroup.OPEN_SEARCH])
class InfraConfig
