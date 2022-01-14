package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.platform.TargetPlatform

open class DeepCopyComponentContainerContributor : StorageComponentContainerContributor, PluginAvailability {
    override fun registerModuleComponents(
        container: StorageComponentContainer, platform: TargetPlatform, moduleDescriptor: ModuleDescriptor
    ) {
        if (moduleDescriptor.isDeepCopyPluginEnabled()) {
            container.useInstance(DeepCopyCollectionElementChecker())
        }
    }
}
