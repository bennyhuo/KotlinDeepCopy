package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.logger.Logger
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import kotlinx.metadata.*
import kotlinx.metadata.jvm.KotlinClassMetadata
import com.squareup.kotlinpoet.ClassName as KClassName

class KClassMirror(kotlinClassMetadata: KotlinClassMetadata.Class) {

    data class Component(val name: String, val type: TypeName) {

        val typeElement: KTypeElement? by lazy {
            when (type) {
                is ParameterizedTypeName -> KTypeElement.from(type.rawType.canonicalName)
                is com.squareup.kotlinpoet.ClassName -> KTypeElement.from(type.canonicalName)
                else -> throw IllegalArgumentException("Illegal type: $type")
            }
        }
    }

    var isData: Boolean = false
        private set

    val components = mutableListOf<Component>()

    val typeParameters = mutableListOf<KmTypeParameterVisitorImpl>()

    init {
        kotlinClassMetadata.accept(object : KmClassVisitor() {
            override fun visit(flags: Flags, name: ClassName) {
                super.visit(flags, name)
                isData = Flag.Class.IS_DATA(flags)
            }

            override fun visitTypeParameter(
                flags: Flags,
                name: String,
                id: Int,
                variance: KmVariance
            ): KmTypeParameterVisitor? {
                return KmTypeParameterVisitorImpl(flags, name, id, variance).also { typeParameters += it }
            }

            override fun visitConstructor(flags: Flags): KmConstructorVisitor? {
                if (Flag.Constructor.IS_PRIMARY(flags)) {
                    return object : KmConstructorVisitor() {
                        override fun visitValueParameter(
                            flags: Flags,
                            parameterName: String
                        ): KmValueParameterVisitor? {
                            return object : KmValueParameterVisitor() {
                                override fun visitType(flags: Flags): KmTypeVisitor? {
                                    return object: KmTypeVisitorImpl(flags, typeParameters){
                                        override fun visitEnd() {
                                            super.visitEnd()
                                            components += Component(parameterName, type)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return super.visitConstructor(flags)
            }
        })
    }

    override fun toString(): String {
        return "isData=$isData, components=${components.joinToString()}"
    }
}