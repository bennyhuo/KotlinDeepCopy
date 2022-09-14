package com.bennyhuo.kotlin.deepcopy.ide

import com.bennyhuo.kotlin.kcp.BuildConfig
import org.gradle.api.Project
import org.jetbrains.plugins.gradle.tooling.ErrorMessageBuilder
import org.jetbrains.plugins.gradle.tooling.ModelBuilderService
import java.io.Serializable

/**
 * Created by benny at 2022/1/13 8:14 AM.
 */
class DeepCopyModelBuilder : ModelBuilderService {
    override fun buildAll(modelName: String?, project: Project): Any {
        val deepCopyPlugin = project.plugins.findPlugin(BuildConfig.KOTLIN_PLUGIN_ID)
        return DeepCopyGradleModelImpl(deepCopyPlugin != null)
    }

    override fun canBuild(modelName: String?): Boolean {
        return modelName == DeepCopyGradleModel::class.java.name
    }

    override fun getErrorMessageBuilder(project: Project, e: Exception): ErrorMessageBuilder {
        return ErrorMessageBuilder.create(project, e, "Gradle import errors")
            .withDescription("Unable to build ${BuildConfig.KOTLIN_PLUGIN_ID} plugin configuration")
    }
}

interface DeepCopyGradleModel : Serializable {
    val isEnabled: Boolean
}

class DeepCopyGradleModelImpl(override val isEnabled: Boolean) : DeepCopyGradleModel
