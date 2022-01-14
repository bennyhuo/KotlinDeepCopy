package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.checker.SimpleClassicTypeSystemContext.isPrimitiveType
import org.jetbrains.kotlin.types.typeUtil.supertypes

/**
 * Created by benny at 2022/1/14 3:49 PM.
 */
class DeepCopyCollectionElementChecker : DeclarationChecker {
    override fun check(
        declaration: KtDeclaration,
        descriptor: DeclarationDescriptor,
        context: DeclarationCheckerContext
    ) {
        if (
            descriptor is ClassDescriptor
            && declaration is KtClass
            && descriptor.isData
            && (descriptor.implementsDeepCopiableInterface() || descriptor.annotatedAsDeepCopiableDataClass())
        ) {
            val parameterDeclarations = declaration.primaryConstructorParameters
            descriptor.unsubstitutedPrimaryConstructor
                ?.valueParameters
                ?.forEachIndexed { index, value ->
                    val userType = parameterDeclarations[index].typeReference?.userType()
                    checkCollection(value.type, false, context, userType)
                }
        }
    }

    private fun PsiElement.userType() = getChildOfType<KtUserType>()

    private fun checkCollection(
        type: KotlinType,
        shouldReport: Boolean,
        context: DeclarationCheckerContext,
        userType: KtUserType?
    ) {
        if (userType == null) return

        if (type is SimpleType) {
            if (type.isPrimitiveType()) return

            if (type.getJetTypeFqName(false) in collectionTypes) {
                val typeArgument = userType.typeArguments.single().typeReference?.userType() ?: return
                checkCollection(type.arguments.single().type, true, context, typeArgument)
                return
            }

            if (shouldReport) {
                if (type.supertypes().none {
                        it.getJetTypeFqName(false) == DEEP_COPY_INTERFACE_NAME
                    }) {
                    context.trace.report(
                        ErrorsDeepCopy.ELEMENT_NOT_IMPLEMENT_DEEPCOPIABLE.on(userType, type.toString())
                    )
                }
            }
        }
    }
}