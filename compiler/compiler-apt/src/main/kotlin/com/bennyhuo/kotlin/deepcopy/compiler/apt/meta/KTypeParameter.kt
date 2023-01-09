package com.bennyhuo.kotlin.deepcopy.compiler.apt.meta

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeVariableName
import kotlinx.metadata.Flags
import kotlinx.metadata.KmTypeParameterVisitor
import kotlinx.metadata.KmTypeVisitor
import kotlinx.metadata.KmVariance

class KTypeParameter(
    val flags: Flags,
    val name: String,
    val id: Int,
    val variance: KmVariance,
    val kTypeCreator: (flags: Flags) -> KType
) : KmTypeParameterVisitor() {

    val upperBounds = ArrayList<KType>()

    val typeVariableNameWithoutVariance by lazy {
        if (upperBounds.isEmpty()) {
            TypeVariableName(name)
        } else {
            TypeVariableName(name, upperBounds.map { it.type })
        }
    }

    val typeVariableName by lazy {
        val variance = when(variance){
            KmVariance.INVARIANT -> null
            KmVariance.IN -> KModifier.IN
            KmVariance.OUT -> KModifier.OUT
        }

        if (upperBounds.isEmpty()) {
            TypeVariableName(name, variance)
        } else {
            TypeVariableName(name, upperBounds.map { it.type }, variance)
        }
    }

    override fun visitUpperBound(flags: Flags): KmTypeVisitor {
        return kTypeCreator(flags).also { upperBounds += it }
    }
}