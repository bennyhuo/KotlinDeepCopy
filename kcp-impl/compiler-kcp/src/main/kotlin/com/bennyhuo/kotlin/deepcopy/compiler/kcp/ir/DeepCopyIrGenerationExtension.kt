package com.bennyhuo.kotlin.deepcopy.compiler.kcp.ir

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.PluginAvailability
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

open class DeepCopyIrGenerationExtension : IrGenerationExtension, PluginAvailability {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.transformChildrenVoid(DeepCopyClassTransformer(pluginContext, this))
    }
}