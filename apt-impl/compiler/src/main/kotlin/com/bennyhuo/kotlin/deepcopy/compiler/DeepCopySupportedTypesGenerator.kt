package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.utils.writeToFile
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass

class DeepCopySupportedTypesGenerator {
    companion object {
        private const val PACKAGE_NAME = "com.bennyhuo.kotlin.deepcopy"
        private const val SIMPLE_NAME = "DeepCopy"
    }

    fun generate(kTypeElements: List<KTypeElement>) {
        val fileSpecBuilder = FileSpec.builder(PACKAGE_NAME, SIMPLE_NAME)
        val objectBuilder = TypeSpec.objectBuilder(SIMPLE_NAME)
        val propertyBuilder = PropertySpec.builder(
            "supportedTypes",
            Set::class.asClassName().parameterizedBy(KClass::class.asClassName().parameterizedBy(STAR))
        )
            .mutable(false)
            .addAnnotation(JvmStatic::class)
            .initializer("hashSetOf(${kTypeElements.map { "%T::class" }.joinToString(",\n")})", *kTypeElements.map {
                if (it.kotlinClassName is ParameterizedTypeName) it.kotlinClassName.rawType else it.kotlinClassName
            }.toTypedArray())

        fileSpecBuilder.addType(objectBuilder.addProperty(propertyBuilder.build()).build()).build().writeToFile()
    }
}