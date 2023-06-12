package com.bennyhuo.kotlin.deepcopy.compiler.kcp

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.checker.DeepCopyComponentContainerContributor
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.ir.DeepCopyIrGenerationExtension
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.symbol.DeepCopyResolveExtension
import com.google.auto.service.AutoService
import com.intellij.mock.MockProject
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

@OptIn(ExperimentalCompilerApi::class)
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


