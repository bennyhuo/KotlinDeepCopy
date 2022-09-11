package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.js.inline.util.zipWithDefault
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeAttributes
import org.jetbrains.kotlin.types.TypeProjectionImpl

/**
 * Created by benny at 2021/6/25 8:01.
 */
open class DeepCopyResolveExtension : SyntheticResolveExtension, PluginAvailability {

    override fun addSyntheticSupertypes(
        thisDescriptor: ClassDescriptor,
        supertypes: MutableList<KotlinType>
    ) {
        if (thisDescriptor.isDeepCopyPluginEnabled() && thisDescriptor.annotatedAsDeepCopyableDataClass()) {
            supertypes.add(
                KotlinTypeFactory.simpleNotNullType(
                    TypeAttributes.Empty,
                    thisDescriptor.module.deepCopyableType(),
                    listOf(TypeProjectionImpl(thisDescriptor.defaultType))
                )
            )
        } else {
            super.addSyntheticSupertypes(thisDescriptor, supertypes)
        }
    }

    override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
        if (thisDescriptor.isDeepCopyPluginEnabled() && thisDescriptor.annotatedAsDeepCopyableDataClass()) {
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
                if (thisDescriptor.annotatedAsDeepCopyableDataClass()
                    && result.none {
                        it.typeParameters.isEmpty() && it.valueParameters.zipWithDefault(
                            thisDescriptor.unsubstitutedPrimaryConstructor!!.valueParameters, null
                        ).all { it.first?.type == it.second.type }
                    }) {
                    result += DeepCopyFunctionDescriptorImpl(thisDescriptor).apply {
                        initialize(thisDescriptor.unsubstitutedPrimaryConstructor!!.valueParameters.map {
                            it.copy(this, declaresDefaultValue = true)
                        })
                    }
                }

                // data class & DeepCopyable<T>
                if (thisDescriptor.isData && thisDescriptor.implementsDeepCopyableInterface()
                    && result.none { it.typeParameters.isEmpty() && it.valueParameters.isEmpty() }
                ) {
                    result += DeepCopyFunctionDescriptorImpl(thisDescriptor).apply {
                        initialize()
                    }
                }
            }
        }
    }
}