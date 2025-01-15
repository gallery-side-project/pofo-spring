package org.pofo.api.common.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringAutowireConstructorExtension
import io.kotest.extensions.spring.SpringExtension

object KotestConfig : AbstractProjectConfig() {
    override fun extensions(): List<Extension> = listOf(SpringExtension, SpringAutowireConstructorExtension)

    override val isolationMode: IsolationMode
        get() = IsolationMode.InstancePerLeaf
}
