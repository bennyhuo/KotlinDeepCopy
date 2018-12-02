package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.logger.Logger
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeVariableName
import kotlinx.metadata.*

class KmTypeParameterVisitorImpl(
    val flags: Flags,
    val name: String,
    val id: Int,
    val variance: KmVariance
) : KmTypeParameterVisitor() {

    var upperBounds: KmTypeVisitorImpl? = null

    val typeVariableNameWithoutVariance by lazy {
        TypeVariableName(name).let { typeVariableName ->
            upperBounds?.let {
                typeVariableName.withBounds(it.type)
            }?: typeVariableName
        }
    }

    val typeVariableName by lazy {
        val variance = when(variance){
            KmVariance.INVARIANT -> null
            KmVariance.IN -> KModifier.IN
            KmVariance.OUT -> KModifier.OUT
        }

        TypeVariableName(name, variance).let { typeVariableName ->
            upperBounds?.let {
                typeVariableName.withBounds(it.type)
            }?: typeVariableName
        }
    }

    override fun visitEnd() {
        super.visitEnd()
    }

    override fun visitExtensions(type: KmExtensionType): KmTypeParameterExtensionVisitor? {
        return super.visitExtensions(type)
    }

    override fun visitUpperBound(flags: Flags): KmTypeVisitor? {
        return KmTypeVisitorImpl(flags).also { upperBounds = it }
    }
}