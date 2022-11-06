package com.bennyhuo.kotlin.deepcopy.compiler.apt

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig
import com.bennyhuo.kotlin.deepcopy.compiler.apt.loop.DeepCopyLoopDetector
import com.bennyhuo.kotlin.deepcopy.compiler.apt.meta.KTypeElement
import com.bennyhuo.kotlin.processor.module.apt.AptModuleProcessor
import com.bennyhuo.kotlin.processor.module.common.MODULE_MIXED
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes(
    "com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy",
    "com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig"
)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class DeepCopyProcessor : AptModuleProcessor() {
    override val annotationsForIndex = setOf(DeepCopyConfig::class.java.name)

    override val processorName: String = "deepCopy"

    override val supportedModuleTypes: Set<Int> = setOf(MODULE_MIXED)

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        AptContext.init(processingEnv)
    }

    override fun processMain(
        roundEnv: RoundEnvironment,
        annotatedSymbols: Map<String, Set<Element>>,
        annotatedSymbolsFromLibrary: Map<String, Set<Element>>,
    ) {
        val configIndex = DeepCopyConfigIndex(
            annotatedSymbols[DeepCopyConfig::class.java.name],
            annotatedSymbolsFromLibrary[DeepCopyConfig::class.java.name],
        )

        roundEnv.getElementsAnnotatedWith(DeepCopy::class.java)
            .filterIsInstance<TypeElement>()
            .filter { it.kind.isClass }
            .plus(configIndex.deepCopyClassesFromConfig)
            .map {
                KTypeElement.from(it)
            }.forEach {
                DeepCopyLoopDetector(it).detect()
                DeepCopyGenerator(it).generate()
            }
        DeepCopyConfigIndex.release()
    }
}