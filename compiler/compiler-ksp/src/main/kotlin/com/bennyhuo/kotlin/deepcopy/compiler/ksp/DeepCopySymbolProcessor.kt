package com.bennyhuo.kotlin.deepcopy.compiler.ksp

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.utils.LoggerMixin
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier

/**
 * Created by benny at 2021/6/20 19:02.
 */
class DeepCopySymbolProcessor(
    override val env: SymbolProcessorEnvironment
) : SymbolProcessor, LoggerMixin {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        try {
            logger.warn("DeepCopySymbolProcessor, ${KotlinVersion.CURRENT}")
            val index = Index(env, resolver)
            index.generateCurrent()

            logger.warn("typesFromCurrentIndex: ${index.typesFromCurrentIndex.joinToString { it.simpleName.asString() }}")

            val deepCopyTypes =
                resolver.getSymbolsWithAnnotation(DeepCopy::class.qualifiedName!!)
                    .filterIsInstance<KSClassDeclaration>()
                    .filter { Modifier.DATA in it.modifiers }
                    .toSet() + index.typesFromCurrentIndex

            logger.warn("DeepCopyTypes: ${deepCopyTypes.joinToString { it.simpleName.asString() }}")
            DeepCopyGenerator(env).generate(resolver, deepCopyTypes)
        } catch (e: Exception) {
            logger.exception(e)
        }
        return emptyList()
    }
}