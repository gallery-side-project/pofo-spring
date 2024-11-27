package org.pofo.infra.importer

import org.springframework.context.annotation.DeferredImportSelector
import org.springframework.core.type.AnnotationMetadata

class PofoInfraConfigImportSelector : DeferredImportSelector {
    override fun selectImports(metadata: AnnotationMetadata): Array<String> {
        return getGroups(metadata)
            .map {
                    infraConfigGroup ->
                infraConfigGroup.configClass.name
            }.toTypedArray()
    }

    private fun getGroups(metadata: AnnotationMetadata): Array<PofoInfraConfigGroup> {
        val attributes = metadata.getAnnotationAttributes(EnablePofoInfraConfig::class.java.name) ?: return arrayOf()
        return attributes["value"] as Array<PofoInfraConfigGroup>
    }
}
