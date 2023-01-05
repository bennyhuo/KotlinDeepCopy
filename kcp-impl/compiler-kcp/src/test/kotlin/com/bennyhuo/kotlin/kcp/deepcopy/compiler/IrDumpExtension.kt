package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.dumpKotlinLike

class IrDumpExtension : IrGenerationExtension {

    var rawIr: String = ""
    var kotlinLikeIr: String = ""

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        rawIr = moduleFragment.dump()
        kotlinLikeIr = moduleFragment.dumpKotlinLike()
    }
}

class IrDumpComponentRegistrar : ComponentRegistrar {

    val irDumpExtension = IrDumpExtension()

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        IrGenerationExtension.registerExtension(
            project,
            irDumpExtension
        )
    }
}
