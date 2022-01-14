package com.bennyhuo.kotlin.deepcopy.ide

import com.bennyhuo.kotlin.kcp.deepcopy.compiler.DeepCopyComponentContainerContributor
import org.jetbrains.kotlin.analyzer.moduleInfo
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.idea.caches.project.ModuleSourceInfo
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

class IdeDeepCopyComponentContainerContributor : DeepCopyComponentContainerContributor() {
    override fun ModuleDescriptor.isDeepCopyPluginEnabled(): Boolean {
        return moduleInfo.safeAs<ModuleSourceInfo>()
            ?.module?.let(DeepCopyAvailability::isAvailable)
            ?: false
    }
}
