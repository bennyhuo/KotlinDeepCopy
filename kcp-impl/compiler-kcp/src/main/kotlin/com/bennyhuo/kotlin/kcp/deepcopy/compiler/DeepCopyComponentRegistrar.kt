package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

@AutoService(ComponentRegistrar::class)
class DeepCopyComponentRegistrar : ComponentRegistrar {

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        SyntheticResolveExtension.registerExtension(
            project,
            DeepCopyResolveExtension()
        )
        IrGenerationExtension.registerExtension(
            project,
            DeepCopyIrGenerationExtension()
        )
        StorageComponentContainerContributor.registerExtension(
            project,
            DeepCopyComponentContainerContributor()
        )
    }
}


