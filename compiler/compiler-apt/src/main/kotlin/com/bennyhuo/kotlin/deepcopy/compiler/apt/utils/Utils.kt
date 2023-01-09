package com.bennyhuo.kotlin.deepcopy.compiler.apt.utils

import com.bennyhuo.aptutils.types.ClassType
import com.bennyhuo.aptutils.types.packageName
import com.bennyhuo.kotlin.deepcopy.compiler.apt.meta.KTypeElement
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.lang.model.element.TypeElement
import kotlin.contracts.contract

fun Metadata.parse() = KotlinClassMetadata.read(
    KotlinClassHeader(
        this.kind,
        this.metadataVersion,
        this.data1, this.data2, this.extraString, this.packageName, this.extraInt
    )
)

val kotlinCollectionTypeToJvmType = mapOf(
    //builtins
    "kotlin.collections.Collection" to "java.util.Collection",
    "kotlin.collections.MutableCollection" to "java.util.Collection",
    "kotlin.collections.List" to "java.util.List",
    "kotlin.collections.MutableList" to "java.util.List",
    "kotlin.collections.Set" to "java.util.Set",
    "kotlin.collections.MutableSet" to "java.util.Set",
    "kotlin.collections.Map" to "java.util.Map",
    "kotlin.collections.MutableMap" to "java.util.Map",

    //typealiases
    "kotlin.collections.ArrayList" to "java.util.ArrayList",
    "kotlin.collections.LinkedHashSet" to "java.util.LinkedHashSet",
    "kotlin.collections.HashSet" to "java.util.HashSet",
    "kotlin.collections.LinkedHashMap" to "java.util.LinkedHashMap",
    "kotlin.collections.HashMap" to "java.util.HashMap"
)

fun mapKotlinCollectionTypeToJvmType(type: ParameterizedTypeName): ParameterizedTypeName {
    val mappedType = when (type.rawType.canonicalName) {
        "kotlin.collections.Collection",
        "kotlin.collections.MutableCollection" -> ClassType("java.util.Collection")

        "kotlin.collections.List",
        "kotlin.collections.MutableList" -> ClassType("java.util.List")

        "kotlin.collections.Set",
        "kotlin.collections.MutableSet" -> ClassType("java.util.Set")

        "kotlin.collections.Map",
        "kotlin.collections.MutableMap" -> ClassType("java.util.Map")

        else -> null
    }
    return (mappedType?.kotlin as? ClassName)?.parameterizedBy(*type.typeArguments.toTypedArray())
        ?: type
}

inline fun escapeStdlibPackageName(packageName: String) =
    if (packageName == "kotlin") "com.bennyhuo.kotlin.deepcopy.builtin" else packageName


val KTypeElement.escapedPackageName: String
    get() = escapeStdlibPackageName(packageName())

private val supportedCollectionTypes = setOf(
    "java.util.Collection",
    "java.util.List",
    "java.util.Set"
)

private val supportedMapTypes = setOf(
    "java.util.Map"
)

val TypeElement.isSupportedCollectionType: Boolean
    get() = this.qualifiedName.toString() in supportedCollectionTypes

val TypeElement.isSupportedMapType: Boolean
    get() = this.qualifiedName.toString() in supportedMapTypes

const val RUNTIME_PACKAGE = "com.bennyhuo.kotlin.deepcopy.runtime"

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