package com.bennyhuo.kotlin.deepcopy.ide

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.checker.DeepCopyComponentContainerContributor
import org.jetbrains.kotlin.analyzer.moduleInfo
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.idea.base.projectStructure.moduleInfo.ModuleSourceInfo

class IdeDeepCopyComponentContainerContributor : DeepCopyComponentContainerContributor() {
    override fun ModuleDescriptor.isDeepCopyPluginEnabled(): Boolean {
        return moduleInfo.safeAs<ModuleSourceInfo>()
            ?.module?.let(DeepCopyAvailability::isAvailable)
            ?: false
    }
}
