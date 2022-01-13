package com.bennyhuo.kotlin.deepcopy.ide

import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.module.Module
import org.jetbrains.plugins.gradle.service.project.GradleProjectResolverUtil
import org.jetbrains.plugins.gradle.util.GradleConstants

class GradleDeepCopyAvailabilityProvider : DeepCopyAvailabilityProvider {
    override fun isAvailable(module: Module): Boolean {
        val path = ExternalSystemApiUtil.getExternalProjectPath(module) ?: return false
        val externalProjectInfo = ExternalSystemUtil.getExternalProjectInfo(module.project, GradleConstants.SYSTEM_ID, path) ?: return false
        val moduleData = GradleProjectResolverUtil.findModule(externalProjectInfo.externalProjectStructure, path) ?: return false
        return ExternalSystemApiUtil.find(moduleData, DeepCopyIdeModel.KEY)?.data?.isEnabled ?: false
    }
}