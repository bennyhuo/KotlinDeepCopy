package com.bennyhuo.kotlin.deepcopy.compiler

import javax.lang.model.element.AnnotationMirror
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.TypeVisitor

object JavaArrayType: TypeMirror {
    override fun getKind(): TypeKind = TypeKind.ARRAY

    override fun <R : Any?, P : Any?> accept(v: TypeVisitor<R, P>, p: P): R {
        return v.visit(this)
    }

    override fun <A : Annotation?> getAnnotationsByType(annotationType: Class<A>?) =
        emptyArray<Any>() as Array<A>

    override fun <A : Annotation?> getAnnotation(annotationType: Class<A>?) = null

    override fun getAnnotationMirrors(): MutableList<out AnnotationMirror> = mutableListOf()

}