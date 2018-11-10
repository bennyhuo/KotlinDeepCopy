package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes("com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy", "kotlin.Metadata")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
class DeepCopyProcessor : AbstractProcessor() {

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        AptContext.init(processingEnv)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(DeepCopy::class.java)
            .filter { it.kind.isClass }
            .mapNotNull { (it as? TypeElement)?.let(::KTypeElement) }
            .forEach {
                DeepCopyGenerator(it).generate()
            }
        return true
    }
}