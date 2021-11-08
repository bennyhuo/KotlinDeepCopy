package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.types.asElement
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyIndex
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

class Index(roundEnvironment: RoundEnvironment) {

    companion object {
        val instance: Index
            get() = currentInstance!!

        private var currentInstance: Index? = null

        operator fun contains(declaration: KTypeElement): Boolean {
            return declaration in instance.typesFromCurrentIndex ||
                    declaration in instance.typesFromLibraryIndex
        }

        fun release() {
            currentInstance = null
        }
    }

    init {
        currentInstance = this
    }

    val typesFromLibraryIndex by lazy {
        AptContext.elements.getPackageElement(IndexGenerator.INDEX_PACKAGE)
            .enclosedElements
            .filterIsInstance<TypeElement>()
            .map {
                it.getAnnotation(DeepCopyIndex::class.java)
            }.flatMap {
                it.values.toList()
            }.mapNotNull {
                AptContext.elements.getTypeElement(it)
            }.toSet()
//            .onEach {
//                //logger.warn(">>> ${it.qualifiedName!!.asString()}")
//            }
    }

    val typesFromCurrentIndex by lazy {
        currentConfigs.flatMap {
            it.annotationMirrors
        }.flatMap {
            it.elementValues.values
        }.flatMap {
            when (val value = it.value) {
                is List<*> -> value
                else -> listOf(value)
            }
        }.filterIsInstance<TypeMirror>()
            .map { it.asElement() }
            .filterIsInstance<TypeElement>()
            .toSet()
    }

    val currentConfigs by lazy {
        roundEnvironment.getElementsAnnotatedWith(DeepCopyConfig::class.java)
            .filterIsInstance<TypeElement>()
    }

    fun generateCurrent() {
        IndexGenerator().generate(
            typesFromCurrentIndex, currentConfigs.mapNotNull { it.enclosingElement }.toList()
        )
    }

}