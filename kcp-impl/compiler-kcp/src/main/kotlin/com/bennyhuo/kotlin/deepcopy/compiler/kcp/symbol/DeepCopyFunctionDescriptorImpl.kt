package com.bennyhuo.kotlin.deepcopy.compiler.kcp.symbol

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.DEEP_COPY_FUNCTION_NAME
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.name.Name

/**
 * Created by benny at 2022/1/10 6:42 PM.
 */
class DeepCopyFunctionDescriptorImpl(
    private val classDescriptor: ClassDescriptor
) : SimpleFunctionDescriptorImpl(
    classDescriptor,
    null,
    Annotations.EMPTY,
    Name.identifier(DEEP_COPY_FUNCTION_NAME),
    CallableMemberDescriptor.Kind.SYNTHESIZED,
    classDescriptor.source
) {
    fun initialize(
        valueParameters: List<ValueParameterDescriptor> = emptyList()
    ) {
        super.initialize(
            null,
            classDescriptor.thisAsReceiverParameter,
            emptyList(),
            valueParameters,
            classDescriptor.defaultType,
            Modality.FINAL,
            DescriptorVisibilities.PUBLIC
        )
    }
}