package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.DataClassDescriptorResolver
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

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
        if (thisDescriptor.isData && name.identifier == "deepCopy") {
            result += createCopyFunctionDescriptor(
                thisDescriptor.unsubstitutedPrimaryConstructor?.valueParameters!!,
                thisDescriptor
            )
        }
    }

    fun createCopyFunctionDescriptor(
        constructorParameters: Collection<ValueParameterDescriptor>,
        classDescriptor: ClassDescriptor,
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
            val parameterDescriptor = ValueParameterDescriptorImpl(
                functionDescriptor, null, parameter.index, parameter.annotations, parameter.name, parameter.type, true,
                parameter.isCrossinline, parameter.isNoinline, parameter.varargElementType, parameter.source
            )
            parameterDescriptors.add(parameterDescriptor)
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
        return functionDescriptor
    }
}