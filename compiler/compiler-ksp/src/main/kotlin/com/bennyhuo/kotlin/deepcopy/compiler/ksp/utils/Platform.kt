package com.bennyhuo.kotlin.deepcopy.compiler.ksp.utils

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver

/**
 * Created by benny.
 */
class Platform(private val resolver: Resolver) {

    val isKotlinJs by lazy {
        resolver.getClassDeclarationByName("kotlin.js.JsName") != null
    }

    val isKotlinNative by lazy {
        resolver.getClassDeclarationByName("kotlin.native.CName") != null
    }

    val isKotlinJvm by lazy {
        !isKotlinNative && !isKotlinJs
    }

}