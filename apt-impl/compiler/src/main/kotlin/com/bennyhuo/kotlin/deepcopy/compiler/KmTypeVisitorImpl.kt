package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.logger.Logger
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
import kotlinx.metadata.*
import kotlinx.metadata.jvm.JvmTypeExtensionVisitor

open class KmTypeVisitorImpl(val flags: Flags,  val typeParametersInContainer: List<KmTypeParameterVisitorImpl> = emptyList(), val variance: KmVariance = KmVariance.INVARIANT, val typeFlexibilityId: String? = null) :
    KmTypeVisitor() {

    private var name: ClassName = ""

    val rawType: com.squareup.kotlinpoet.ClassName by lazy {
        val splits = name.split("/")
        assert(splits.size > 1)
        val packageName = splits.subList(0, splits.size - 1).joinToString(".")
        val simpleNames = splits.last().split("\\.").toTypedArray()
        val simpleName = simpleNames[0]
        val otherSimpleNames = simpleNames.sliceArray(1 until simpleNames.size)
        com.squareup.kotlinpoet.ClassName(packageName, simpleName, *otherSimpleNames)
    }

    val type: TypeName by lazy {
        if(abbreviatedTypeVisitor != null) {
            abbreviatedTypeVisitor!!.type
        }
        else if(typeParameters.isEmpty()) rawType
        else rawType.parameterizedBy(*(typeParameters.map { it.wildcardTypeName }.toTypedArray()))
    }

    val wildcardTypeName by lazy {
        when(this.variance){
            KmVariance.INVARIANT -> type
            KmVariance.IN -> WildcardTypeName.supertypeOf(type)
            KmVariance.OUT -> WildcardTypeName.subtypeOf(type)
        }
    }

    private val typeParameters = ArrayList<KmTypeVisitorImpl>()

    private val upperBounds = ArrayList<KmTypeVisitorImpl>()

    private var abbreviatedTypeVisitor: KmTypeVisitorImpl? = null

    override fun visitAbbreviatedType(flags: Flags): KmTypeVisitor? {
        return object :KmTypeVisitorImpl(flags, typeParametersInContainer){
            override fun visitEnd() {
                super.visitEnd()
            }
        }.also {
            abbreviatedTypeVisitor = it
        }
    }

    override fun visitArgument(flags: Flags, variance: KmVariance): KmTypeVisitor? {
        return KmTypeVisitorImpl(flags, typeParametersInContainer, variance).also {
            typeParameters += it
        }
    }

    override fun visitClass(name: ClassName) {
        super.visitClass(name)
        this.name = name
    }

    override fun visitFlexibleTypeUpperBound(flags: Flags, typeFlexibilityId: String?): KmTypeVisitor? {
        return KmTypeVisitorImpl(flags, typeParametersInContainer, variance, typeFlexibilityId).also { upperBounds  += it }
    }

    override fun visitStarProjection() {
        super.visitStarProjection()
        typeParameters += KmTypeVisitorImpl(0, typeParametersInContainer).also { it.visitClass("*") }
    }

    override fun visitTypeAlias(name: ClassName) {
        super.visitTypeAlias(name)
        this.name = name
    }

    override fun visitTypeParameter(id: Int) {
        super.visitTypeParameter(id)
        this.name = typeParametersInContainer[id].name
    }
}