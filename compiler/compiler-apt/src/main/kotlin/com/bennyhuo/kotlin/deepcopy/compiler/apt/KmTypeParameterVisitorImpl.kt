package com.bennyhuo.kotlin.deepcopy.compiler.apt

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeVariableName
import kotlinx.metadata.Flags
import kotlinx.metadata.KmTypeParameterVisitor
import kotlinx.metadata.KmTypeVisitor
import kotlinx.metadata.KmVariance

class KmTypeParameterVisitorImpl(
    val flags: Flags,
    val name: String,
    val id: Int,
    val variance: KmVariance
) : KmTypeParameterVisitor() {

    var upperBounds: KmTypeVisitorImpl? = null

    val typeVariableNameWithoutVariance by lazy {
        upperBounds?.let {
            TypeVariableName(name, it.type)
        }?: TypeVariableName(name)
    }

    val typeVariableName by lazy {
        val variance = when(variance){
            KmVariance.INVARIANT -> null
            KmVariance.IN -> KModifier.IN
            KmVariance.OUT -> KModifier.OUT
        }

        upperBounds?.let {
            TypeVariableName(name, it.type, variance = variance)
        }?: TypeVariableName(name, variance)
    }

    override fun visitUpperBound(flags: Flags): KmTypeVisitor? {
        return KmTypeVisitorImpl(flags).also { upperBounds = it }
    }
}