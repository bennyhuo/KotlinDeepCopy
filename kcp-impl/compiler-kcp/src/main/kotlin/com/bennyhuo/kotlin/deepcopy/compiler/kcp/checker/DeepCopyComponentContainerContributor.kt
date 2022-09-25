package com.bennyhuo.kotlin.deepcopy.compiler.kcp.checker

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.PluginAvailability
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.platform.TargetPlatform

open class DeepCopyComponentContainerContributor : StorageComponentContainerContributor,
    PluginAvailability {
    override fun registerModuleComponents(
        container: StorageComponentContainer, platform: TargetPlatform, moduleDescriptor: ModuleDescriptor
    ) {
        if (moduleDescriptor.isDeepCopyPluginEnabled()) {
            container.useInstance(DeepCopyDeclarationChecker())
        }
    }
}
