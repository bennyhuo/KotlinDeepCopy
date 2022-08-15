package com.bennyhuo.kotlin.deepcopy.compiler.apt.meta

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
}