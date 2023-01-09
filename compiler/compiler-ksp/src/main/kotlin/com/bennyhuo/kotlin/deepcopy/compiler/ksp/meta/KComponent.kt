package com.bennyhuo.kotlin.deepcopy.compiler.ksp.meta

import com.bennyhuo.kotlin.deepcopy.compiler.ksp.deepCopyable
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.isDeepCopyTypeVariable
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.TypeName

/**
 * Created by benny.
 */
class KComponent(
    val valueParameter: KSValueParameter,
    val type: TypeName
) {

    val name: String
        get() = valueParameter.name!!.asString()

    val ksType by lazy {
        valueParameter.type.resolve()
    }

    val declaration by lazy {
        ksType.declaration
    }

    val isDeepCopyableClass: Boolean by lazy {
        declaration.deepCopyable
    }

    val isDeepCopyableTypeVariable: Boolean by lazy {
        type.isDeepCopyTypeVariable()
    }

    val isDeepCopyable: Boolean by lazy {
        isDeepCopyableClass || isDeepCopyableTypeVariable
    }

    fun isTypeArgumentDeepCopyableClass(index: Int): Boolean {
        if (ksType.arguments.isEmpty()) return false

        return ksType.arguments[index].let {arg ->
            arg.type?.resolve()?.declaration?.deepCopyable == true
        }
    }

    fun isTypeArgumentDeepCopyable(index: Int): Boolean {
        if (ksType.arguments.isEmpty()) return false

        return ksType.arguments[index].let {arg ->
            arg.type?.resolve()?.declaration?.let {
                it.deepCopyable || it.isDeepCopyTypeVariable()
            } == true
        }
    }

}