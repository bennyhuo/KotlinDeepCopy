package com.bennyhuo.kotlin.deepcopy

import kotlin.math.abs

/**
 * Created by benny.
 */
interface DeepCopyable

fun <T : DeepCopyable> T.deepCopy(): T {
    val constructor = this::class.js.asDynamic()
    val parameters = (1..Int.MAX_VALUE).asSequence().map {
        componentFunction(constructor.prototype, "component${it}")
    }.takeWhile {
        it !== undefined
    }.map {
        it.call(this).unsafeCast<Any>()
            .let {
                (it as? DeepCopyable)?.deepCopy() ?: it
            }
    }.toList().toTypedArray()

    val newInstance = js("{}")
    newInstance.__proto__ = constructor.prototype
    constructor.apply(newInstance, parameters)
    return newInstance as T
}

private fun componentFunction(
    prototype: dynamic, name: String
) = prototype[name]
    ?: prototype["${name}_${abs(name.hashCode()).toString(36)}_k$"]
    ?: prototype["${name}_0_k$"]