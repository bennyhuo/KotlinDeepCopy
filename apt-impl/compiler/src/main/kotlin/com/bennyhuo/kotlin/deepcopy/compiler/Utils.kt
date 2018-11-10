package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.AptContext
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata

fun KTypeElement(className: String): KTypeElement? = AptContext.elements.getTypeElement(className)?.let(::KTypeElement)

fun Metadata.parse() = KotlinClassMetadata.read(
    KotlinClassHeader(
        this.kind,
        this.metadataVersion,
        this.bytecodeVersion, this.data1, this.data2, this.extraString, this.packageName, this.extraInt
    )
)