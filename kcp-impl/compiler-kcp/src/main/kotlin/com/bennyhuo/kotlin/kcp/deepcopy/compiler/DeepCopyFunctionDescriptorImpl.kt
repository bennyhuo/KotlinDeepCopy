package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.name.Name

/**
 * Created by benny at 2022/1/10 6:42 PM.
 */
class DeepCopyFunctionDescriptorImpl(
    containingDeclaration: DeclarationDescriptor,
    annotations: Annotations,
    name: Name,
    kind: CallableMemberDescriptor.Kind,
    source: SourceElement
) : SimpleFunctionDescriptorImpl(containingDeclaration, null, annotations, name, kind, source)