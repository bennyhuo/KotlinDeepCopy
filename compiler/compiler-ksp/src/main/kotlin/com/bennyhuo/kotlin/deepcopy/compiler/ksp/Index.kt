package com.bennyhuo.kotlin.deepcopy.compiler.ksp

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyIndex
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.utils.LoggerMixin
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType

class Index(
    override val env: SymbolProcessorEnvironment,
    resolver: Resolver
) : LoggerMixin {

    companion object {
        val instance: Index
            get() = currentInstance!!

        private var currentInstance: Index? = null

        operator fun contains(declaration: KSDeclaration): Boolean {
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
        resolver.getDeclarationsFromPackage(IndexGenerator.INDEX_PACKAGE)
            .filterIsInstance<KSClassDeclaration>()
            .flatMap {
                it.getAnnotationsByType(DeepCopyIndex::class)
            }.flatMap {
                it.values.asSequence()
            }.mapNotNull {
                resolver.getClassDeclarationByName(it)
            }.toSet()
    }

    val typesFromCurrentIndex by lazy {
        currentConfigs.flatMap {
            it.annotations
        }.flatMap {
            it.arguments
        }.flatMap {
            when (val value = it.value) {
                is List<*> -> value.asSequence()
                else -> sequenceOf(value)
            }
        }.filterIsInstance<KSType>()
            .map { it.declaration }
            .filterIsInstance<KSClassDeclaration>()
            .toSet()
    }

    val currentConfigs by lazy {
        resolver.getSymbolsWithAnnotation(DeepCopyConfig::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
    }

    fun generateCurrent() {
        IndexGenerator(env).generate(
            typesFromCurrentIndex, currentConfigs.mapNotNull { it.containingFile }.toList()
        )
    }

}