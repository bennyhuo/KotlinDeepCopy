package com.bennyhuo.kotlin.deepcopy.compiler.apt.meta

import com.bennyhuo.kotlin.deepcopy.compiler.apt.utils.isDeepCopyTypeVariable
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

data class KComponent(val name: String, val type: TypeName) {

    val typeElement: KTypeElement? by lazy {
        KTypeElement.from(type)
    }

    val typeArgumentElements: List<KTypeElement?> by lazy {
        if (type is ParameterizedTypeName) {
            type.typeArguments.map {
                KTypeElement.from(it)
            }
        } else {
            emptyList()
        }
    }

    val typeArguments: List<TypeName> by lazy {
        if (type is ParameterizedTypeName) {
            type.typeArguments
        } else {
            emptyList()
        }
    }

    val isDeepCopyableClass: Boolean by lazy {
        typeElement.isDeepCopyable()
    }

    val isDeepCopyableTypeVariable: Boolean by lazy {
        type.isDeepCopyTypeVariable()
    }

    val isDeepCopyable: Boolean by lazy {
        isDeepCopyableClass || isDeepCopyableTypeVariable
    }

    fun isTypeArgumentDeepCopyable(index: Int): Boolean {
        return if (type is ParameterizedTypeName) {
            type.typeArguments[index].let {
                KTypeElement.from(it).isDeepCopyable() || it.isDeepCopyTypeVariable()
            }
        } else {
            false
        }
    }
}