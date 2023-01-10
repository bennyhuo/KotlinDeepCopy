package com.bennyhuo.kotlin.deepcopy.compiler.kcp

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.superTypes
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.types.KotlinType

/**
 * Created by benny at 2022/1/10 8:36 AM.
 */
const val DEEP_COPY_FUNCTION_NAME = "deepCopy"
const val DEEP_COPY_ANNOTATION_NAME = "com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy"
const val DEEP_COPY_INTERFACE_NAME = "com.bennyhuo.kotlin.deepcopy.DeepCopyable"

val collectionTypes = arrayOf(
    "kotlin.collections.Iterable", "kotlin.collections.MutableIterable",
    "kotlin.collections.Collection", "kotlin.collections.MutableCollection",
    "kotlin.collections.List", "kotlin.collections.MutableList",
    "kotlin.collections.Set", "kotlin.collections.MutableSet"
)

fun PsiElement.userType() = getChildOfType<KtUserType>()

fun IrClass.annotatedAsDeepCopyableDataClass(): Boolean {
    return isData && this.hasAnnotation(FqName(DEEP_COPY_ANNOTATION_NAME))
}

private fun List<IrType>.containsDeepCopyableInterface(): Boolean {
    return find { it.classFqName?.asString() == DEEP_COPY_INTERFACE_NAME } != null
}

fun IrClass.implementsDeepCopyableInterface(): Boolean {
    return this.superTypes.containsDeepCopyableInterface()
}

fun IrType.implementsDeepCopyableInterface(): Boolean {
    return this.superTypes().containsDeepCopyableInterface()
}

fun IrClass.deepCopyFunctionForDataClass(): IrFunction? {
    if (!annotatedAsDeepCopyableDataClass()) return null

    return functions.singleOrNull {
        it.name.identifier == DEEP_COPY_FUNCTION_NAME
                && (primaryConstructor?.valueParameters?.matchWith(it.valueParameters) ?: true)
    }
}

fun IrClass.deepCopyFunctionForCollections(pluginContext: IrPluginContext): IrFunction? {
    val fqName = defaultType.classFqName?.asString()
    if (fqName in collectionTypes) {
        return pluginContext.referenceFunctions(FqName("com.bennyhuo.kotlin.deepcopy.deepCopy"))
            .singleOrNull {
                it.owner.extensionReceiverParameter?.type?.classFqName?.asString() == fqName
            }?.owner
    }
    return null
}

fun IrClass.copyFunctionForDataClass(): IrFunction? {
    if (!isData) return null

    return functions.singleOrNull {
        it.name.identifier == "copy"
                && (primaryConstructor?.valueParameters?.matchWith(it.valueParameters) ?: true)
    }
}

fun IrType.deepCopyFunctionForDeepCopyable(pluginContext: IrPluginContext): IrFunction? {
    if (!implementsDeepCopyableInterface()) return null
    val deepCopyable = pluginContext.referenceClass(FqName(DEEP_COPY_INTERFACE_NAME))

    return deepCopyable?.functions?.singleOrNull {
        it.owner.name.identifier == DEEP_COPY_FUNCTION_NAME && it.owner.valueParameters.isEmpty()
    }?.owner
}

fun IrClass.deepCopyFunctionForDeepCopyable(): IrFunction? {
    if (!implementsDeepCopyableInterface()) return null

    return functions.singleOrNull {
        it.name.identifier == DEEP_COPY_FUNCTION_NAME && it.valueParameters.isEmpty()
    }
}

fun List<IrValueParameter>.matchWith(valueParameters: List<IrValueParameter>): Boolean {
    if (this.size != valueParameters.size) return false

    return this.zip(valueParameters).all { it.first.type == it.second.type }
}

fun ClassDescriptor.annotatedAsDeepCopyableDataClass(): Boolean {
    return isData && this.annotations.hasAnnotation(FqName(DEEP_COPY_ANNOTATION_NAME))
}

fun ClassDescriptor.implementsDeepCopyableInterface(): Boolean {
    return this.getSuperInterfaces()
        .find { it.fqNameUnsafe.asString() == DEEP_COPY_INTERFACE_NAME } != null
}

fun ValueParameterDescriptor.copy(
    containingDeclaration: CallableDescriptor = this.containingDeclaration,
    original: ValueParameterDescriptor? = this.original,
    index: Int = this.index,
    annotations: Annotations = this.annotations,
    name: Name = this.name,
    outType: KotlinType = this.type,
    declaresDefaultValue: Boolean = this.declaresDefaultValue(),
    isCrossinline: Boolean = this.isCrossinline,
    isNoinline: Boolean = this.isNoinline,
    varargElementType: KotlinType? = this.varargElementType,
    source: SourceElement = this.source
): ValueParameterDescriptor {
    return ValueParameterDescriptorImpl(
        containingDeclaration,
        original,
        index,
        annotations,
        name,
        outType,
        declaresDefaultValue,
        isCrossinline,
        isNoinline,
        varargElementType,
        source,
    )
}

internal fun ModuleDescriptor.deepCopyableType() =
    requireNotNull(
        findClassAcrossModuleDependencies(
            ClassId(
                FqName("com.bennyhuo.kotlin.deepcopy"),
                Name.identifier("DeepCopyable")
            )
        )
    ) { "Can't locate class $DEEP_COPY_INTERFACE_NAME" }

internal fun IrFunction.irThis(): IrExpression {
    val irDispatchReceiverParameter = dispatchReceiverParameter!!
    return IrGetValueImpl(
        startOffset, endOffset,
        irDispatchReceiverParameter.type,
        irDispatchReceiverParameter.symbol
    )
}

internal fun IrBuilderWithScope.irGetProperty(
    receiver: IrExpression,
    property: IrProperty
): IrExpression {
    // In some JVM-specific cases, such as when 'allopen' compiler plugin is applied,
    // data classes and corresponding properties can be non-final.
    // We should use getters for such properties (see KT-41284).
    val backingField = property.backingField
    return if (property.modality == Modality.FINAL && backingField != null) {
        irGetField(receiver, backingField)
    } else {
        irCall(property.getter!!).apply {
            dispatchReceiver = receiver
        }
    }
}