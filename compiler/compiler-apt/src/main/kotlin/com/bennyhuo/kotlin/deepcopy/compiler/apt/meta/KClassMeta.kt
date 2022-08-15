package com.bennyhuo.kotlin.deepcopy.compiler.apt.meta

import kotlinx.metadata.*
import kotlinx.metadata.jvm.KotlinClassMetadata

class KClassMeta(kotlinClassMetadata: KotlinClassMetadata.Class) {

    var isData: Boolean = false
        private set

    val components = mutableListOf<KComponent>()

    val typeParameters = mutableListOf<KTypeParameter>()

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
            ): KmTypeParameterVisitor {
                return KTypeParameter(flags, name, id, variance).also { typeParameters += it }
            }

            override fun visitConstructor(flags: Flags): KmConstructorVisitor? {
                if (!Flag.Constructor.IS_SECONDARY(flags)) {
                    return object : KmConstructorVisitor() {
                        override fun visitValueParameter(
                            flags: Flags,
                            name: String
                        ): KmValueParameterVisitor {
                            return object : KmValueParameterVisitor() {
                                override fun visitType(flags: Flags): KmTypeVisitor {
                                    return object: KType(flags, typeParameters){
                                        override fun visitEnd() {
                                            super.visitEnd()
                                            components += KComponent(name, type)
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