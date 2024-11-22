package org.pofo.infra.importer

import org.springframework.context.annotation.Import

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(PofoInfraConfigImportSelector::class)
annotation class EnablePofoInfraConfig(
    val value: Array<PofoInfraConfigGroup>,
)
