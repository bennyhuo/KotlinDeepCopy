package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.FqName

/**
 * Created by benny at 2022/1/10 8:36 AM.
 */
const val DEEP_COPY_FUNCTION_NAME = "deepCopy"
const val DEEP_COPY_CLASS_NAME = "com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy"
val deepCopyFqName = FqName(DEEP_COPY_CLASS_NAME)

fun IrClass.isDeepCopiable(): Boolean {
    return isData && this.hasAnnotation(deepCopyFqName)
}

fun IrClass.deepCopyFunction(): IrFunction? {
    if (!isDeepCopiable()) return null

    return functions.singleOrNull {
        it.name.identifier == DEEP_COPY_FUNCTION_NAME
                && (primaryConstructor?.valueParameters?.matchWith(it.valueParameters) ?: true)
    }
}

fun List<IrValueParameter>.matchWith(valueParameters: List<IrValueParameter>): Boolean {
    if (this.size != valueParameters.size) return false

    return this.zip(valueParameters).all { it.first.type == it.second.type }
}

fun ClassDescriptor.isDeepCopiable(): Boolean {
    return isData && this.annotations.hasAnnotation(deepCopyFqName)
}