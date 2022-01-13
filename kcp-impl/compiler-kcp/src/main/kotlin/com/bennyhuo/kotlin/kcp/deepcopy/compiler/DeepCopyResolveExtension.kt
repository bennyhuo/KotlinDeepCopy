package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

/**
 * Created by benny at 2021/6/25 8:01.
 */
open class DeepCopyResolveExtension : SyntheticResolveExtension, PluginAvailability {

    override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
        if (thisDescriptor.isDeepCopyPluginEnabled() && thisDescriptor.annotatedAsDeepCopiableDataClass()) {
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
        if (thisDescriptor.isDeepCopyPluginEnabled()) {
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
}