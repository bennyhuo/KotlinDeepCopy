package com.bennyhuo.kotlin.deepcopy.reflect

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

fun <T : Any> T.deepCopy(): T {
    if (!this::class.isData) {
        return this
    }

    return this::class.primaryConstructor!!.let { primaryConstructor ->
        primaryConstructor.parameters.associateWith { parameter ->
            (this::class as KClass<T>).declaredMemberProperties
                .first { it.name == parameter.name }
                .get(this)
                ?.deepCopy()
        }.let(primaryConstructor::callBy)
    }
}
