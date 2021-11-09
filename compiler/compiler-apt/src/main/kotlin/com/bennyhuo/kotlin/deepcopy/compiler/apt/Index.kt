package com.bennyhuo.kotlin.deepcopy.compiler.apt

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.logger.Logger
import com.bennyhuo.aptutils.types.asElement
import com.bennyhuo.aptutils.types.asTypeMirror
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyIndex
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypesException

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
            .onEach {
                Logger.warn(">>> ${it.qualifiedName}")
            }
    }

    val typesFromCurrentIndex by lazy {
        currentConfigs.asSequence().map {
            it.getAnnotation(DeepCopyConfig::class.java)
        }.flatMap {
            try {
                it.values.map { it.asTypeMirror() }
            } catch (e: MirroredTypesException) {
                e.typeMirrors
            }
        }.map { it.asElement() }
            .filterIsInstance<TypeElement>()
            .toSet()
            .onEach {
                Logger.warn("current index >>> ${it.qualifiedName}")
            }
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