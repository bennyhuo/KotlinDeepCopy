package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.backend.common.lower.copyAsValueParameter
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

/**
 * Created by benny at 2021/6/25 8:01.
 */
class DeepCopyResolveExtension : SyntheticResolveExtension {

    override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
        if (thisDescriptor.annotatedAsDeepCopiableDataClass()) {
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
        if (name.identifier == DEEP_COPY_FUNCTION_NAME) {
            // @DeepCopy
            if (thisDescriptor.annotatedAsDeepCopiableDataClass()) {
                result += DeepCopyFunctionDescriptorImpl(thisDescriptor).apply {
                    initialize(thisDescriptor.unsubstitutedPrimaryConstructor!!.valueParameters.map {
                        it.copy(this, declaresDefaultValue = true)
                    })
                }
            }

            // : DeepCopiable<T>
            if (thisDescriptor.implementsDeepCopiableInterface()) {
                result += DeepCopyFunctionDescriptorImpl(thisDescriptor).apply {
                    initialize()
                }
            }
        }
    }
}