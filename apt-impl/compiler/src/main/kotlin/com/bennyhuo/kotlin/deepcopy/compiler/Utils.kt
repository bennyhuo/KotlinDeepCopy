package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.types.ClassType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata

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
    return (mappedType?.kotlin as? ClassName)?.parameterizedBy(*type.typeArguments.toTypedArray()) ?: type
}

inline fun escapeStdlibPackageName(packageName: String) =
    if (packageName == "kotlin") "com.bennyhuo.kotlin.deepcopy.builtin" else packageName