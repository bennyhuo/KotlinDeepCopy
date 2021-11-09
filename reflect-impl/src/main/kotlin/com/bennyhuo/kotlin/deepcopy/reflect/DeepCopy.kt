package com.bennyhuo.kotlin.deepcopy.reflect

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

interface DeepCopyable

fun <T : DeepCopyable> T.deepCopy(): T {
    if (!this::class.isData) return this

    val thisClass = (this::class as KClass<T>)
    return thisClass.primaryConstructor!!.let { primaryConstructor ->
        primaryConstructor.parameters.associateWith { parameter ->
            thisClass.declaredMemberProperties
                .first { it.name == parameter.name }
                .get(this)
                ?.let {
                    (it as? DeepCopyable)?.deepCopy() ?: it
                }
        }.let(primaryConstructor::callBy)
    }
}