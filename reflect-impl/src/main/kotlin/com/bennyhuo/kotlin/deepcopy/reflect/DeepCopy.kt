package com.bennyhuo.kotlin.deepcopy.reflect

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

fun <T : Any> T.deepCopy(): T {
    if (!this::class.isData) {
        return this
    }

    return this::class.primaryConstructor!!.let { primaryConstructor ->
        primaryConstructor.parameters
            .map { parameter ->
                val value =
                    (this::class as KClass<T>).declaredMemberProperties.first { it.name == parameter.name }.get(this)
                if ((parameter.type.classifier as? KClass<*>)?.isData == true) {
                    parameter to value?.deepCopy()
                } else {
                    parameter to value
                }
            }
            .toMap()
            .let(primaryConstructor::callBy)
    }
}
