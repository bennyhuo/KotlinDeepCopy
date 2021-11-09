package com.bennyhuo.kotlin.deepcopy.compiler.ksp

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier

/**
 * Created by benny at 2021/6/20 19:02.
 */
class DeepCopySymbolProcessor(private val environment: SymbolProcessorEnvironment) :
    SymbolProcessor {
    private val logger = environment.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        KspContext.environment = environment
        KspContext.resolver = resolver
        
        try {
            logger.warn("DeepCopySymbolProcessor, ${KotlinVersion.CURRENT}")
            val index = Index(resolver)
            index.generateCurrent()
            
            logger.warn("typesFromCurrentIndex: ${index.typesFromCurrentIndex.joinToString { it.simpleName.asString() }}")

            val deepCopyTypes =
                resolver.getSymbolsWithAnnotation(DeepCopy::class.qualifiedName!!)
                    .filterIsInstance<KSClassDeclaration>()
                    .filter { Modifier.DATA in it.modifiers }
                    .toSet() + index.typesFromCurrentIndex

            logger.warn("DeepCopyTypes: ${deepCopyTypes.joinToString { it.simpleName.asString() }}")
            DeepCopyGenerator().generate(deepCopyTypes)
        } catch (e: Exception) {
            logger.exception(e)
        }
        return emptyList()
    }
}