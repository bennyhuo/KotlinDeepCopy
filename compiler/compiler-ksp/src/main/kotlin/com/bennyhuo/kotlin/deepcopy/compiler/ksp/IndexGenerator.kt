package com.bennyhuo.kotlin.deepcopy.compiler.ksp

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyIndex
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo

/**
 * Created by benny.
 */
class IndexGenerator {

    companion object {
        const val INDEX_PACKAGE = "com.bennyhuo.kotlin.deepcopy"
    }

    fun generate(deepCopyTypes: Set<KSClassDeclaration>, dependencies: List<KSFile>) {
        if (deepCopyTypes.isEmpty()) return
        
        val indexName = "DeepCopy_${generateName(deepCopyTypes)}"
        FileSpec.builder(
            INDEX_PACKAGE,
            indexName
        ).addType(
            TypeSpec.classBuilder(indexName)
                .addAnnotation(
                    AnnotationSpec.builder(DeepCopyIndex::class)
                        .addMember(
                            "values = [${deepCopyTypes.joinToString { "%S" }}]",
                            *deepCopyTypes.map { it.qualifiedName!!.asString() }.toTypedArray()
                        ).build()
                ).also { typeBuilder ->
                    dependencies.forEach { 
                            typeBuilder.addOriginatingKSFile(it)
                    }
                }
                .build()
        ).build().writeTo(KspContext.environment.codeGenerator, true)
    }

    private fun generateName(deepCopyTypes: Set<KSClassDeclaration>): String {
        return deepCopyTypes.map { it.qualifiedName!!.asString() }
            .sortedBy { it }
            .joinToString("_")
            .replace('.', '_')
            .let { 
                if (it.length > 200) {
                    it.substring(0, 200)
                } else it
            }
            .also {
                logger.warn("Index name: $it")
            }
    }

}