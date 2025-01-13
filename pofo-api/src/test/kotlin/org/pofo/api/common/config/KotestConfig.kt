package org.pofo.api.common.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringAutowireConstructorExtension
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode

class KotestConfig : AbstractProjectConfig() {
    override val parallelism = 3

    override fun extensions(): List<Extension> =
        listOf(SpringTestExtension(SpringTestLifecycleMode.Root), SpringAutowireConstructorExtension)

    override val isolationMode: IsolationMode
        get() = IsolationMode.InstancePerLeaf
}
