package org.pofo.infra.importer

import org.pofo.infra.elasticsearch.config.OpenSearchConfig

enum class PofoInfraConfigGroup(
    val configClass: Class<out PofoInfraConfig>,
) {
    OPEN_SEARCH(OpenSearchConfig::class.java),
}
