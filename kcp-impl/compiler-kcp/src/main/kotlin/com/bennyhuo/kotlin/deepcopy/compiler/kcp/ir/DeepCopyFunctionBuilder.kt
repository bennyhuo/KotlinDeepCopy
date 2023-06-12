package com.bennyhuo.kotlin.deepcopy.compiler.kcp.ir

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.copyFunctionForDataClass
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.deepCopyFunctionForCollections
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.deepCopyFunctionForDataClass
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.deepCopyFunctionForDeepCopyable
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.isTypeParameter
import org.jetbrains.kotlin.ir.util.primaryConstructor

class DeepCopyFunctionBuilder(
    private val irClass: IrClass,
    private val irFunction: IrFunction,
    private val pluginContext: IrPluginContext,
    startOffset: Int = SYNTHETIC_OFFSET,
    endOffset: Int = SYNTHETIC_OFFSET,
) : IrBlockBodyBuilder(pluginContext, Scope(irFunction.symbol), startOffset, endOffset) {

    init {
        irFunction.body = doBuild()
    }

    fun generateDefaultParameter(
        valueMapper: DeepCopyFunctionBuilder.(IrValueParameter) -> IrExpressionBody?
    ): DeepCopyFunctionBuilder {
        irFunction.valueParameters.forEach { irValueParameter ->
            irValueParameter.defaultValue = valueMapper(irValueParameter)
        }
        return this
    }

    fun generateBody(
        valueParameterMapper: DeepCopyFunctionBuilder.(IrValueParameter) -> IrExpression
    ): DeepCopyFunctionBuilder {
        val primaryConstructor = irClass.primaryConstructor!!
        +irReturn(
            irCall(
                primaryConstructor.symbol,
                irClass.defaultType,
                constructedClass = irClass
            ).apply {
                symbol.owner.valueParameters.forEachIndexed { index, param ->
                    putValueArgument(index, param.type.tryDeepCopy(valueParameterMapper(param)))
                }
            }
        )
        return this
    }

    private fun IrType.tryDeepCopy(
        irExpression: IrExpression
    ): IrExpression {
        if (this.isTypeParameter()) {
            val deepCopyFunction = this.deepCopyFunctionForDeepCopyable(pluginContext)
            if (deepCopyFunction != null) {
                return irCall(deepCopyFunction).apply {
                    dispatchReceiver = irExpression
                }
            }
        }

        val irClass = this.getClass() ?: return irExpression
        with(irClass) {
            val possibleCopyFunction = deepCopyFunctionForDataClass()
                ?: deepCopyFunctionForDeepCopyable()
                ?: copyFunctionForDataClass()

            return if (possibleCopyFunction != null) {
                irCall(possibleCopyFunction).apply {
                    dispatchReceiver = irExpression
                }
            } else {
                val deepCopyFunction = deepCopyFunctionForCollections(pluginContext)
                if (deepCopyFunction == null) {
                    irExpression
                } else {
                    irCall(deepCopyFunction).apply {
                        extensionReceiver = irExpression
                    }
                }
            }
        }
    }

}
