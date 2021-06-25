/*
 * Copyright (C) 2020 Brian Norman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.toLogger
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetFieldImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.interpreter.toIrConst
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker

class DeepCopyIrGenerationExtension(
    private val messageCollector: MessageCollector
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        messageCollector.toLogger().warning(moduleFragment.dump())
        moduleFragment.transformChildrenVoid(DeepCopyClassTransformer(pluginContext))

        messageCollector.toLogger().warning("------------------")
        messageCollector.toLogger().warning(moduleFragment.dump())
        throw IllegalArgumentException()
    }

    fun IrModuleFragment.dumpIrToFile() {

    }
}

@OptIn(ObsoleteDescriptorBasedAPI::class)
class DeepCopyClassTransformer(val pluginContext: IrPluginContext) :
    IrElementTransformerVoidWithContext() {
    override fun visitClassNew(declaration: IrClass): IrStatement {
        val result = super.visitClassNew(declaration)
        if (declaration.isData) {
            println("data class: ${declaration.name}")
            val function = declaration.addFunction("deepCopy", declaration.defaultType)

            val functionBuilder = MemberFunctionBuilder(declaration, function, pluginContext)
            functionBuilder.build {
                declaration.primaryConstructor?.valueParameters?.forEach { valueParameter ->
                    println("${valueParameter.name}: ${valueParameter.type.classFqName}| ${valueParameter.type.annotations}")
                    irFunction.addValueParameter(valueParameter.name, valueParameter.type).apply {
                        defaultValue = pluginContext.irFactory.createExpressionBody(
                            irGetProperty(irThis(), declaration.properties.first { it.name == valueParameter.name })
                        )
                    }
                }

                generateCopyFunction(declaration.primaryConstructor?.symbol!!)
            }
        } else {
            println("not data class ${declaration.name}")
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
    inline fun addToClass(builder: MemberFunctionBuilder.(IrFunction) -> Unit): IrFunction {
        build(builder)
        irClass.declarations.add(irFunction)
        return irFunction
    }

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

    fun irOther(): IrExpression {
        val irFirstParameter = irFunction.valueParameters[0]
        return IrGetValueImpl(
            startOffset, endOffset,
            irFirstParameter.type,
            irFirstParameter.symbol
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

    fun putDefault(parameter: ValueParameterDescriptor, value: IrExpression) {
        irFunction.putDefault(parameter, irExprBody(value))
    }

    fun generateComponentFunction(irProperty: IrProperty) {
        +irReturn(irGetProperty(irThis(), irProperty))
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
                    irGet(irValueParameter.type, irValueParameter.symbol)
                }
            }
        )
    }

    fun generateEqualsMethodBody(properties: List<IrProperty>) {
        val irType = irClass.defaultType

        if (!irClass.isInline) {
            +irIfThenReturnTrue(irEqeqeq(irThis(), irOther()))
        }
        +irIfThenReturnFalse(irNotIs(irOther(), irType))
        val otherWithCast = irTemporary(irAs(irOther(), irType), "other_with_cast")
        for (property in properties) {
            val arg1 = irGetProperty(irThis(), property)
            val arg2 = irGetProperty(irGet(irType, otherWithCast.symbol), property)
            +irIfThenReturnFalse(irNotEquals(arg1, arg2))
        }
        +irReturnTrue()
    }

    private val intClass = context.builtIns.int
    private val intType = context.builtIns.intType

    private val intTimesSymbol: IrSimpleFunctionSymbol =
        intClass.unsubstitutedMemberScope.findFirstFunction("times") {
            KotlinTypeChecker.DEFAULT.equalTypes(it.valueParameters[0].type, intType)
        }.let { pluginContext.symbolTable.referenceSimpleFunction(it) }

    private val intPlusSymbol: IrSimpleFunctionSymbol =
        intClass.unsubstitutedMemberScope.findFirstFunction("plus") {
            KotlinTypeChecker.DEFAULT.equalTypes(it.valueParameters[0].type, intType)
        }.let { pluginContext.symbolTable.referenceSimpleFunction(it) }

}