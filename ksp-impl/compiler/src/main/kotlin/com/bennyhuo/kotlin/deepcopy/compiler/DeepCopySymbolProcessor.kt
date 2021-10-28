package com.bennyhuo.kotlin.deepcopy.compiler

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
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
            
            val deepCopyConfigs = resolver.getSymbolsWithAnnotation("com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig")
                    .filterIsInstance<KSClassDeclaration>()
            val deepCopyTypeFromConfigs = deepCopyConfigs.flatMap {
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

            val deepCopyTypes =
                resolver.getSymbolsWithAnnotation("com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy")
                    .filterIsInstance<KSClassDeclaration>()
                    .filter { Modifier.DATA in it.modifiers }
                    .toSet() + deepCopyTypeFromConfigs

            logger.warn("DeepCopyTypes: ${deepCopyTypes.joinToString { it.simpleName.asString() }}")
            DeepCopyGenerator().generate(deepCopyTypes)
            DeepCopyIndexGenerator().generate(deepCopyTypeFromConfigs, deepCopyConfigs.mapNotNull { it.containingFile }.toList())
        } catch (e: Exception) {
            logger.exception(e)
        }
        return emptyList()
    }
}