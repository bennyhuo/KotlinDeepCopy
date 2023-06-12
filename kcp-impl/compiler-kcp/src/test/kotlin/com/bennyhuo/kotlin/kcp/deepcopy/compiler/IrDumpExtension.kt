package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
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

@OptIn(ExperimentalCompilerApi::class)
class IrDumpComponentRegistrar : CompilerPluginRegistrar() {

    val irDumpExtension = IrDumpExtension()

    override val supportsK2: Boolean
        get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        IrGenerationExtension.registerExtension(irDumpExtension)
    }
}
