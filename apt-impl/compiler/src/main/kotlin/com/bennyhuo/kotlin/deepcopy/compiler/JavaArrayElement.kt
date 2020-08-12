package com.bennyhuo.kotlin.deepcopy.compiler

import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror

/**
 * We cannot get the real array class symbol from Elements.getTypeElement(...)
 * Use this to make it easy to support array types.
 */
object JavaArrayElement: TypeElement {
    private val name = NameImpl("Array")

    override fun getModifiers(): MutableSet<Modifier> = mutableSetOf()

    override fun getSimpleName(): Name = name

    override fun getKind(): ElementKind = ElementKind.CLASS

    override fun asType(): TypeMirror = JavaArrayType

    override fun getSuperclass(): TypeMirror {
        TODO("Not yet implemented")
    }

    override fun getTypeParameters(): MutableList<out TypeParameterElement> = mutableListOf()

    override fun getQualifiedName(): Name = name

    override fun getEnclosingElement(): Element {
        TODO("Not yet implemented")
    }

    override fun getInterfaces(): MutableList<out TypeMirror> = mutableListOf()

    override fun <R : Any?, P : Any?> accept(v: ElementVisitor<R, P>?, p: P): R {
        TODO("Not yet implemented")
    }

    override fun <A : Annotation?> getAnnotationsByType(annotationType: Class<A>?): Array<A> = emptyArray<Any>() as Array<A>

    override fun <A : Annotation?> getAnnotation(annotationType: Class<A>?): A? = null

    override fun getNestingKind(): NestingKind = NestingKind.TOP_LEVEL

    override fun getAnnotationMirrors(): MutableList<out AnnotationMirror> = mutableListOf()

    override fun getEnclosedElements(): MutableList<out Element> = mutableListOf()

}