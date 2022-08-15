package com.bennyhuo.kotlin.deepcopy.compiler.apt

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.logger.Logger
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.compiler.apt.loop.DeepCopyLoopDetector
import com.bennyhuo.kotlin.deepcopy.compiler.apt.meta.KTypeElement
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes(
    "com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy",
    "com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig", 
    "kotlin.Metadata")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
class DeepCopyProcessor : AbstractProcessor() {

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        AptContext.init(processingEnv)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        
        Logger.warn("apt.....")
        
        val index = Index(roundEnv)
        index.generateCurrent()
        
        roundEnv.getElementsAnnotatedWith(DeepCopy::class.java)
            .filterIsInstance<TypeElement>()
            .filter { it.kind.isClass }
            .plus(Index.instance.typesFromCurrentIndex)
            .map {
                KTypeElement.from(it)
            }.forEach {
                DeepCopyLoopDetector(it).detect()
                DeepCopyGenerator(it).generate()
            }
        return true
    }
}