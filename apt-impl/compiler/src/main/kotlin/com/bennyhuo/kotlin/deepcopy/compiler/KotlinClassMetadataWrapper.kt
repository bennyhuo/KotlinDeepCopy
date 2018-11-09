package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.logger.Logger
import kotlinx.metadata.*
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.lang.model.element.TypeElement
import com.squareup.kotlinpoet.ClassName as KClassName

fun KTypeElement(className: String): KTypeElement? = AptContext.elements.getTypeElement(className)?.let(::KTypeElement)

fun Metadata.parse() = KotlinClassMetadata.read(
    KotlinClassHeader(
        this.kind,
        this.metadataVersion,
        this.bytecodeVersion, this.data1, this.data2, this.extraString, this.packageName, this.extraInt
    )
)

class KClassMetadata(kotlinClassMetadata: KotlinClassMetadata.Class) {

    data class Component(val name: String, val type: String) {
        val typeElement: KTypeElement? by lazy {
            KTypeElement(type.replace('/', '.'))
        }

        val kotlinClassName: KClassName by lazy {
            val splits = name.split("/")
            assert(splits.size > 1)
            val packageName = splits.subList(0, splits.size - 1).joinToString(".")
            val simpleNames = splits.last().split("\\.").toTypedArray()
            val simpleName = simpleNames[0]
            val otherSimpleNames = simpleNames.sliceArray(1 until simpleNames.size)
            KClassName(packageName, simpleName, *otherSimpleNames)
        }
    }

    var isData: Boolean = false
        private set

    val components = mutableListOf<Component>()

    init {
        kotlinClassMetadata.accept(object : KmClassVisitor() {
            override fun visit(flags: Flags, name: ClassName) {
                super.visit(flags, name)
                isData = Flag.Class.IS_DATA(flags)
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
                                    return object : KmTypeVisitor() {
                                        override fun visitClass(name: ClassName) {
                                            super.visitClass(name)
                                            components += Component(parameterName, name)
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