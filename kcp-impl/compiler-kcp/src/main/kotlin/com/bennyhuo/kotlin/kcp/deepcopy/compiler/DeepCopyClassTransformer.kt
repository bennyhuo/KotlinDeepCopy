package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.expressions.mapTypeParameters
import org.jetbrains.kotlin.ir.expressions.mapValueParameters
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.*

@OptIn(ObsoleteDescriptorBasedAPI::class)
class DeepCopyClassTransformer(val pluginContext: IrPluginContext) :
    IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement {
        val result = super.visitClassNew(declaration)

        if (declaration.isDeepCopiable()) {
            declaration.functions.firstOrNull {
                it.descriptor is DeepCopyFunctionDescriptorImpl
            }?.let { function ->
                MemberFunctionBuilder(declaration, function, pluginContext).build {
                    declaration.primaryConstructor?.valueParameters?.forEachIndexed { index, valueParameter ->
                        irFunction.valueParameters[index].defaultValue =
                            pluginContext.irFactory.createExpressionBody(
                                irGetProperty(
                                    irThis(),
                                    declaration.properties.first { it.name == valueParameter.name })
                            )
                    }

                    generateCopyFunction(declaration.primaryConstructor?.symbol!!)
                }
            } ?: throw IllegalStateException("no synthetic deepCopy function found in ${declaration.name}")
        } else {
            println("not deepCopiable: ${declaration.name}")
        }
        return result
    }
}

@OptIn(ObsoleteDescriptorBasedAPI::class)
private class MemberFunctionBuilder(
    val irClass: IrClass,
    val irFunction: IrFunction,
    val pluginContext: IrPluginContext,
    startOffset: Int = SYNTHETIC_OFFSET,
    endOffset: Int = SYNTHETIC_OFFSET,
) : IrBlockBodyBuilder(pluginContext, Scope(irFunction.symbol), startOffset, endOffset) {

    inline fun <T : IrDeclaration> T.buildWithScope(builder: (T) -> Unit): T =
        also { irDeclaration ->
            pluginContext.symbolTable.withReferenceScope(irDeclaration) {
                builder(irDeclaration)
            }
        }

    inline fun build(builder: MemberFunctionBuilder.(IrFunction) -> Unit) {
        irFunction.buildWithScope {
            builder(irFunction)
            irFunction.body = doBuild()
        }
    }

    fun irThis(): IrExpression {
        val irDispatchReceiverParameter = irFunction.dispatchReceiverParameter!!
        return IrGetValueImpl(
            startOffset, endOffset,
            irDispatchReceiverParameter.type,
            irDispatchReceiverParameter.symbol
        )
    }

    fun transform(typeParameterDescriptor: TypeParameterDescriptor): IrType =
        pluginContext.irBuiltIns.anyType

    fun irGetProperty(receiver: IrExpression, property: IrProperty): IrExpression {
        // In some JVM-specific cases, such as when 'allopen' compiler plugin is applied,
        // data classes and corresponding properties can be non-final.
        // We should use getters for such properties (see KT-41284).
        val backingField = property.backingField
        return if (property.modality == Modality.FINAL && backingField != null) {
            irGetField(receiver, backingField)
        } else {
            irCall(property.getter!!).apply {
                dispatchReceiver = receiver
            }
        }
    }

    fun generateCopyFunction(constructorSymbol: IrConstructorSymbol) {
        +irReturn(
            irCall(
                constructorSymbol,
                irClass.defaultType,
                constructedClass = irClass
            ).apply {
                mapTypeParameters(::transform)
                mapValueParameters {
                    val irValueParameter = irFunction.valueParameters[it.index]

                    irValueParameter.type.getClass()?.deepCopyFunction()?.let(::irCall)
                        ?.apply {
                            dispatchReceiver = irGet(irValueParameter.type, irValueParameter.symbol)
                        } ?: irGet(irValueParameter.type, irValueParameter.symbol)
                }
            }
        )
    }
}