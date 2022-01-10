package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

/**
 * Created by benny at 2021/6/25 8:01.
 */
class DeepCopyResolveExtension: SyntheticResolveExtension {

    override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
        if (thisDescriptor.isDeepCopiable()) {
            return listOf(Name.identifier(DEEP_COPY_FUNCTION_NAME))
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
        println("generateSyntheticMethods: ${thisDescriptor.name} - ${name.identifier}")
        if (thisDescriptor.isDeepCopiable() && name.identifier == DEEP_COPY_FUNCTION_NAME) {
            result += createDeepCopyFunctionDescriptor(
                thisDescriptor.unsubstitutedPrimaryConstructor?.valueParameters!!,
                thisDescriptor,
            )
        }
    }

    private fun createDeepCopyFunctionDescriptor(
        constructorParameters: Collection<ValueParameterDescriptor>,
        classDescriptor: ClassDescriptor,
    ): SimpleFunctionDescriptor {

        val functionDescriptor = DeepCopyFunctionDescriptorImpl(
            classDescriptor,
            Annotations.EMPTY,
            Name.identifier("deepCopy"),
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