package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyIndex
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.symbol.KSClassDeclaration

object IndexCollector {

    val typesFromIndex by lazy {
        KspContext.resolver.getDeclarationsFromPackage(IndexGenerator.INDEX_PACKAGE)
            .filterIsInstance<KSClassDeclaration>()
            .flatMap {
                it.getAnnotationsByType(DeepCopyIndex::class)
            }.flatMap {
                it.values.asSequence()
            }.mapNotNull {
                KspContext.resolver.getClassDeclarationByName(it)
            }.toSet()
            .onEach {
                logger.warn(">>> ${it.qualifiedName!!.asString()}")
            }
    }

}