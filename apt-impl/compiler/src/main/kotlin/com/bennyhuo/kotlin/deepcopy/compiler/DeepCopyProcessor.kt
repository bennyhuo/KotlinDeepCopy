package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.types.asElement
import com.bennyhuo.aptutils.types.asTypeMirror
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypesException

@SupportedAnnotationTypes("com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy", "kotlin.Metadata")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
class DeepCopyProcessor : AbstractProcessor() {

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        AptContext.init(processingEnv)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(DeepCopyConfig::class.java)
            .flatMap {
                try {
                    it.getAnnotation(DeepCopyConfig::class.java).values
                        //Error promoted earlier and this is only for type inference.
                        .map { cls -> cls.asTypeMirror() }
                } catch (e: MirroredTypesException) {
                    e.typeMirrors
                }
            }.map {
                it.asElement()
            }.plus(roundEnv.getElementsAnnotatedWith(DeepCopy::class.java))
            .filter { it.kind.isClass }
            .mapNotNull { (it as? TypeElement)?.let(KTypeElement.Companion::from) }
            .also {
                DeepCopySupportedTypesGenerator().generate(it)
            }
            .forEach {
                DeepCopyLoopDetector(it).detect()
                DeepCopyGenerator(it).generate()
            }
        return true
    }
}