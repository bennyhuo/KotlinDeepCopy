package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DataClassDescriptorResolver
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassMemberScope

/**
 * Created by benny at 2021/6/25 8:01.
 */
class DeepCopyResolveExtension: SyntheticResolveExtension {

    override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
        if (thisDescriptor.isData) {
            return listOf(Name.identifier("deepCopy"))
        }
        return super.getSyntheticFunctionNames(thisDescriptor)
    }

    override fun generateSyntheticMethods(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: List<SimpleFunctionDescriptor>,
        result: MutableCollection<SimpleFunctionDescriptor>
    ) {
        if (name.identifier == "deepCopy") {
            result += createCopyFunctionDescriptor(
                thisDescriptor.pri
            )
        }
    }

    fun getPrimaryConstructor(): ClassConstructorDescriptor? =
        (mainScope as LazyClassMemberScope?)?.primaryConstructor?.invoke() ?: primaryConstructor()

    fun createCopyFunctionDescriptor(
        constructorParameters: Collection<ValueParameterDescriptor>,
        classDescriptor: ClassDescriptor,
        trace: BindingTrace
    ): SimpleFunctionDescriptor {
        val functionDescriptor = SimpleFunctionDescriptorImpl.create(
            classDescriptor,
            Annotations.EMPTY,
            DataClassDescriptorResolver.COPY_METHOD_NAME,
            CallableMemberDescriptor.Kind.SYNTHESIZED,
            classDescriptor.source
        )

        val parameterDescriptors = arrayListOf<ValueParameterDescriptor>()

        for (parameter in constructorParameters) {
            val propertyDescriptor = trace.bindingContext.get(BindingContext.VALUE_PARAMETER_AS_PROPERTY, parameter)
            // If a parameter doesn't have the corresponding property, it must not have a default value in the 'copy' function
            val declaresDefaultValue = propertyDescriptor != null
            val parameterDescriptor = ValueParameterDescriptorImpl(
                functionDescriptor, null, parameter.index, parameter.annotations, parameter.name, parameter.type, declaresDefaultValue,
                parameter.isCrossinline, parameter.isNoinline, parameter.varargElementType, parameter.source
            )
            parameterDescriptors.add(parameterDescriptor)
            if (declaresDefaultValue) {
                trace.record(BindingContext.VALUE_PARAMETER_AS_PROPERTY, parameterDescriptor, propertyDescriptor)
            }
        }

        functionDescriptor.initialize(
            null,
            classDescriptor.thisAsReceiverParameter,
            emptyList<TypeParameterDescriptor>(),
            parameterDescriptors,
            classDescriptor.defaultType,
            Modality.FINAL,
            DescriptorVisibilities.PUBLIC
        )

        trace.record(BindingContext.DATA_CLASS_COPY_FUNCTION, classDescriptor, functionDescriptor)
        return functionDescriptor
    }
}