package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyIndex
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * Created by benny.
 */
class IndexGenerator {

    companion object {
        const val INDEX_PACKAGE = "com.bennyhuo.kotlin.deepcopy"
    }

    fun generate(deepCopyTypes: Set<TypeElement>, dependencies: List<Element>) {
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
                            *deepCopyTypes.map { it.qualifiedName.toString() }.toTypedArray()
                        ).build()
                ).also { typeBuilder ->
                    dependencies.forEach { 
                            typeBuilder.addOriginatingElement(it)
                    }
                }
                .build()
        ).build().writeTo(AptContext.filer)
    }

    private fun generateName(deepCopyTypes: Set<TypeElement>): String {
        return deepCopyTypes.map { it.qualifiedName.toString() }
            .sortedBy { it }
            .joinToString("_")
            .replace('.', '_')
            .let { 
                if (it.length > 200) {
                    it.substring(0, 200)
                } else it
            }
//            .also {
//                // logger.warn("Index name: $it")
//            }
    }

}