package com.bennyhuo.kotlin.deepcopy.compiler.kcp.ir

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.PluginAvailability
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.annotatedAsDeepCopyableDataClass
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.deepCopyFunctionForDataClass
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.deepCopyFunctionForDeepCopyable
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.implementsDeepCopyableInterface
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.irGetProperty
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.irThis
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.properties

@OptIn(ObsoleteDescriptorBasedAPI::class)
class DeepCopyClassTransformer(
    private val pluginContext: IrPluginContext,
    private val pluginAvailability: PluginAvailability
) : IrElementTransformerVoidWithContext(), PluginAvailability by pluginAvailability {

    override fun visitClassNew(declaration: IrClass): IrStatement {
        if (!declaration.isDeepCopyPluginEnabled()) return super.visitClassNew(declaration)

        if (declaration.annotatedAsDeepCopyableDataClass()) {
            declaration.deepCopyFunctionForDataClass()?.takeIf {
                it.body == null
            }?.let { function ->
                generateDeepCopyFunctionForDataClass(declaration, function)
            }
        }
        // Only generate implementation for data classes.
        if (declaration.isData && declaration.implementsDeepCopyableInterface()) {
            declaration.deepCopyFunctionForDeepCopyable()?.takeIf {
                it.body == null
            }?.let { function ->
                generateDeepCopyFunctionForDeepCopyable(declaration, function)
            }
        }
        return super.visitClassNew(declaration)
    }

    private fun generateDeepCopyFunctionForDataClass(
        irClass: IrClass,
        irFunction: IrFunction
    ) {
        DeepCopyFunctionBuilder(
            irClass,
            irFunction,
            pluginContext
        ).generateBody { constructorParameter ->
            val irValueParameter = irFunction.valueParameters[constructorParameter.index]
            irGet(irValueParameter.type, irValueParameter.symbol)
        }.generateDefaultParameter { index, parameter ->
            val constructorParameter = irClass.primaryConstructor!!.valueParameters[index]
            pluginContext.irFactory.createExpressionBody(
                irGetProperty(
                    irFunction.irThis(),
                    irClass.properties.first { it.name == constructorParameter.name })
            )
        }
    }

    private fun generateDeepCopyFunctionForDeepCopyable(
        irClass: IrClass,
        irFunction: IrFunction
    ) {
        DeepCopyFunctionBuilder(
            irClass,
            irFunction,
            pluginContext
        ).generateBody { constructorParameter ->
            irGetProperty(
                irFunction.irThis(),
                irClass.properties.first { it.name == constructorParameter.name }
            )
        }
    }

}