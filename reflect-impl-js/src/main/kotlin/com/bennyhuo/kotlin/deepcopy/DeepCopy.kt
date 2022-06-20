package com.bennyhuo.kotlin.deepcopy

import kotlin.math.abs

/**
 * Created by benny.
 */
interface DeepCopyable

fun <T : DeepCopyable> T.deepCopy(): T {
    val constructor = this::class.js.asDynamic()
    val newInstance = js("{}")
    newInstance.__proto__ = constructor.prototype
    val parameters = (1..Int.MAX_VALUE).takeWhile {
        constructor.prototype["component${it}_${hash("component${it}")}_k$"] !== undefined
    }.map {
        constructor.prototype["component${it}_${hash("component${it}")}_k$"].call(this).unsafeCast<Any>()
            .let {
                (it as? DeepCopyable)?.deepCopy() ?: it
            }
    }.toTypedArray()
    constructor.apply(newInstance, parameters)
    return newInstance as T
}

private fun hash(name: String) = abs(name.hashCode()).toString(36)