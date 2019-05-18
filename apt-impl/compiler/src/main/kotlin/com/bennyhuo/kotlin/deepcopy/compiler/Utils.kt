package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.types.ClassType
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata

fun Metadata.parse() = KotlinClassMetadata.read(
    KotlinClassHeader(
        this.kind,
        this.metadataVersion,
        this.bytecodeVersion, this.data1, this.data2, this.extraString, this.packageName, this.extraInt
    )
)

//val MUTABLE_LIST = ClassType("kotlin.collections.MutableList")
//
//fun newInstance(kTypeElement: KTypeElement): String{
//    when{
//
//    }
//}