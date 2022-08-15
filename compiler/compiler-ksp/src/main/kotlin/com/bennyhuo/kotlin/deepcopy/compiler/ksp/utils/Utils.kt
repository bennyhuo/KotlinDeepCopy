package com.bennyhuo.kotlin.deepcopy.compiler.ksp

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration

inline fun escapeStdlibPackageName(packageName: String) =
    if (packageName == "kotlin") "com.bennyhuo.kotlin.deepcopy.builtin" else packageName


val KSDeclaration.escapedPackageName: String
    get() = escapeStdlibPackageName(packageName.asString())

val KSDeclaration.deepCopiable: Boolean
    get() = this is KSClassDeclaration &&
            (isAnnotationPresent(DeepCopy::class) || this in DeepCopyConfigIndex)


private val supportedCollectionTypes = setOf(
    "kotlin.collections.Collection",
    "kotlin.collections.Set",
    "kotlin.collections.List",
    "kotlin.collections.MutableCollection",
    "kotlin.collections.MutableSet",
    "kotlin.collections.MutableList"
)

private val supportedMapTypes = setOf(
    "kotlin.collections.Map",
    "kotlin.collections.MutableMap"
)

val KSDeclaration.isSupportedCollectionType: Boolean
    get() = this.qualifiedName?.asString() in supportedCollectionTypes

val KSDeclaration.isSupportedMapType: Boolean
    get() = this.qualifiedName?.asString() in supportedMapTypes

const val RUNTIME_PACKAGE = "com.bennyhuo.kotlin.deepcopy.runtime"