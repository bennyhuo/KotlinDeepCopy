package com.bennyhuo.kotlin.deepcopy.compiler.ksp

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import kotlin.contracts.contract

inline fun escapeStdlibPackageName(packageName: String) =
    if (packageName == "kotlin") "com.bennyhuo.kotlin.deepcopy.builtin" else packageName


val KSDeclaration.escapedPackageName: String
    get() = escapeStdlibPackageName(packageName.asString())

val KSDeclaration.deepCopyable: Boolean
    get() = this is KSClassDeclaration &&
            (isAnnotationPresent(DeepCopy::class) || this in DeepCopyConfigIndex)


fun KSDeclaration.isDeepCopyTypeVariable(): Boolean {
    contract {
        returns(true) implies (this@isDeepCopyTypeVariable is KSTypeParameter)
    }
    return this is KSTypeParameter && this.bounds.any {
        it.resolve().declaration.qualifiedName?.asString() == DEEPCOPYABLE_FULL_NAME
    }
}

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

val DEEPCOPYABLE_FULL_NAME = "com.bennyhuo.kotlin.deepcopy.DeepCopyable"
val DEEPCOPYABLE_TYPENAME = ClassName.bestGuess("com.bennyhuo.kotlin.deepcopy.DeepCopyable")

fun TypeName.isDeepCopyTypeVariable(): Boolean {
    contract {
        returns(true) implies (this@isDeepCopyTypeVariable is TypeVariableName)
    }
    return (this as? TypeVariableName)?.bounds?.any {
        (it is ClassName && it == DEEPCOPYABLE_TYPENAME) ||
                (it is ParameterizedTypeName && it.rawType == DEEPCOPYABLE_TYPENAME)
    } == true
}