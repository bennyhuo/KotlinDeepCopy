package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.isPrimitiveType
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.types.KotlinType

/**
 * Created by benny at 2022/1/10 8:36 AM.
 */
const val DEEP_COPY_FUNCTION_NAME = "deepCopy"
const val DEEP_COPY_ANNOTATION_NAME = "com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy"
val deepCopyFqName = FqName(DEEP_COPY_ANNOTATION_NAME)

const val DEEP_COPY_INTERFACE_NAME = "com.bennyhuo.kotlin.deepcopy.annotations.DeepCopiable"

fun IrClass.annotatedAsDeepCopiableDataClass(): Boolean {
    return isData && this.hasAnnotation(deepCopyFqName)
}

fun IrClass.implementsDeepCopiableInterface(): Boolean {
    return isData && this.superTypes.find { it.classFqName?.asString() == DEEP_COPY_INTERFACE_NAME } != null
}

fun IrClass.deepCopyFunctionForDataClass(): IrFunction? {
    if (!annotatedAsDeepCopiableDataClass()) return null

    return functions.singleOrNull {
        it.name.identifier == DEEP_COPY_FUNCTION_NAME
                && (primaryConstructor?.valueParameters?.matchWith(it.valueParameters) ?: true)
    }
}

fun IrClass.copyFunctionForDataClass(): IrFunction? {
    if (!isData) return null

    return functions.singleOrNull {
        it.name.identifier == "copy"
                && (primaryConstructor?.valueParameters?.matchWith(it.valueParameters) ?: true)
    }
}

fun IrClass.deepCopyFunctionForDeepCopiable(): IrFunction? {
    if (!implementsDeepCopiableInterface()) return null

    return functions.singleOrNull {
        it.name.identifier == DEEP_COPY_FUNCTION_NAME && it.valueParameters.isEmpty()
    }
}

fun List<IrValueParameter>.matchWith(valueParameters: List<IrValueParameter>): Boolean {
    if (this.size != valueParameters.size) return false

    return this.zip(valueParameters).all { it.first.type == it.second.type }
}

fun ClassDescriptor.annotatedAsDeepCopiableDataClass(): Boolean {
    return isData && this.annotations.hasAnnotation(deepCopyFqName)
}

fun ClassDescriptor.implementsDeepCopiableInterface(): Boolean {
    return this.getSuperInterfaces().find { it.fqNameUnsafe.asString() == DEEP_COPY_INTERFACE_NAME } != null
}

fun ValueParameterDescriptor.copy(
    containingDeclaration: CallableDescriptor = this.containingDeclaration,
    original: ValueParameterDescriptor? = this.original,
    index: Int = this.index,
    annotations: Annotations = this.annotations,
    name: Name = this.name,
    outType: KotlinType = this.type,
    declaresDefaultValue: Boolean = this.declaresDefaultValue(),
    isCrossinline: Boolean = this.isCrossinline,
    isNoinline: Boolean = this.isNoinline,
    varargElementType: KotlinType? = this.varargElementType,
    source: SourceElement = this.source
): ValueParameterDescriptor {
    return ValueParameterDescriptorImpl(
        containingDeclaration,
        original,
        index,
        annotations,
        name,
        outType,
        declaresDefaultValue,
        isCrossinline,
        isNoinline,
        varargElementType,
        source,
    )
}
