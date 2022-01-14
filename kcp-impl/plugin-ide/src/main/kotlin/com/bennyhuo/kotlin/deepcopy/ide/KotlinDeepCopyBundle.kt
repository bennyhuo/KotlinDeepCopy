package com.bennyhuo.kotlin.deepcopy.ide

import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import org.jetbrains.kotlin.util.AbstractKotlinBundle

@NonNls
private const val BUNDLE = "messages.KotlinDeepCopyBundle"

object KotlinDeepCopyBundle : AbstractKotlinBundle(BUNDLE) {
    @Nls
    @JvmStatic
    fun message(@NonNls @PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String =
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        getMessage(key, *params)
}
