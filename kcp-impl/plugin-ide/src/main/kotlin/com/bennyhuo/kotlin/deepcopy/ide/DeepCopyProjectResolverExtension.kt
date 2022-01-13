package com.bennyhuo.kotlin.deepcopy.ide

import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.Key
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.project.AbstractExternalEntityData
import com.intellij.openapi.externalSystem.model.project.ModuleData
import com.intellij.openapi.externalSystem.service.project.manage.AbstractProjectDataService
import com.intellij.serialization.PropertyMapping
import org.gradle.tooling.model.idea.IdeaModule
import org.jetbrains.plugins.gradle.service.project.AbstractProjectResolverExtension
import org.jetbrains.plugins.gradle.util.GradleConstants

/**
 * Created by benny at 2022/1/13 8:13 AM.
 */
class DeepCopyIdeModel @PropertyMapping("isEnabled") constructor(
    val isEnabled: Boolean
) : AbstractExternalEntityData(GradleConstants.SYSTEM_ID) {
    companion object {
        val KEY = Key.create(DeepCopyIdeModel::class.java, ProjectKeys.CONTENT_ROOT.processingWeight + 1)
    }
}

class DeepCopyIdeModelDataService : AbstractProjectDataService<DeepCopyIdeModel, Void>() {
    override fun getTargetDataKey() = DeepCopyIdeModel.KEY
}

class DeepCopyProjectResolverExtension : AbstractProjectResolverExtension() {

    override fun getExtraProjectModelClasses() = setOf(DeepCopyGradleModel::class.java)
    override fun getToolingExtensionsClasses() = setOf(DeepCopyModelBuilder::class.java, Unit::class.java)

    override fun populateModuleExtraModels(gradleModule: IdeaModule, ideModule: DataNode<ModuleData>) {
        val deepCopyGradleModel = resolverCtx.getExtraProject(gradleModule, DeepCopyGradleModel::class.java)

        if (deepCopyGradleModel != null && deepCopyGradleModel.isEnabled) {
            ideModule.createChild(DeepCopyIdeModel.KEY, DeepCopyIdeModel(isEnabled = deepCopyGradleModel.isEnabled))
        }

        super.populateModuleExtraModels(gradleModule, ideModule)
    }
}