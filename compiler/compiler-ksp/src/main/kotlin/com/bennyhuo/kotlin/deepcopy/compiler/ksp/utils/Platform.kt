package com.bennyhuo.kotlin.deepcopy.compiler.ksp.utils

import com.google.devtools.ksp.processing.JvmPlatformInfo
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

/**
 * Created by benny.
 */
class Platform(private val env: SymbolProcessorEnvironment) {

    val isKotlinJvm by lazy {
        env.platforms.singleOrNull { it is JvmPlatformInfo } != null
    }

}