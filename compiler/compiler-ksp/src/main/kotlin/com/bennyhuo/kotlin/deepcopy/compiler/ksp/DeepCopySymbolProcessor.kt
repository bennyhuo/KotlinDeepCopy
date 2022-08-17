package com.bennyhuo.kotlin.deepcopy.compiler.ksp

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.utils.LoggerMixin
import com.bennyhuo.kotlin.processor.module.common.MODULE_MIXED
import com.bennyhuo.kotlin.processor.module.ksp.KspModuleProcessor
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier

/**
 * Created by benny at 2021/6/20 19:02.
 */
class DeepCopySymbolProcessor(
    env: SymbolProcessorEnvironment
) : KspModuleProcessor(env), LoggerMixin {
    override val annotationsForIndex = setOf(DeepCopyConfig::class.java.name)

    override val processorName: String = "deepCopy"

    override val supportedModuleTypes: Set<Int> = setOf(MODULE_MIXED)

    override fun processMain(
        resolver: Resolver,
        annotatedSymbols: Map<String, Set<KSAnnotated>>
    ): List<KSAnnotated> {
        try {
            val configIndex = DeepCopyConfigIndex(annotatedSymbols[DeepCopyConfig::class.java.name])

            val deepCopyTypes =
                resolver.getSymbolsWithAnnotation(DeepCopy::class.java.name)
                    .filterIsInstance<KSClassDeclaration>()
                    .filter { Modifier.DATA in it.modifiers }
                    .toSet() + configIndex.deepCopyClassDeclarations

            logger.warn("DeepCopyTypes: ${deepCopyTypes.joinToString { it.simpleName.asString() }}")
            DeepCopyGenerator(env).generate(resolver, deepCopyTypes)

            DeepCopyConfigIndex.release()
        } catch (e: Exception) {
            logger.exception(e)
        }

        return emptyList()
    }

}