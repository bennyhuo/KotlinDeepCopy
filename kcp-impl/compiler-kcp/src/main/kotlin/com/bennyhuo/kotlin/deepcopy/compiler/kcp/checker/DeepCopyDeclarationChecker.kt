package com.bennyhuo.kotlin.deepcopy.compiler.kcp.checker

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.annotatedAsDeepCopyableDataClass
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.collectionTypes
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.implementsDeepCopyableInterface
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.utils.isDeepCopyable
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
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

/**
 * Created by benny at 2022/1/14 3:49 PM.
 */
class DeepCopyDeclarationChecker : DeclarationChecker {
    override fun check(
        declaration: KtDeclaration,
        descriptor: DeclarationDescriptor,
        context: DeclarationCheckerContext
    ) {
        if (
            descriptor is ClassDescriptor
            && declaration is KtClass
            && descriptor.isData
            && (descriptor.implementsDeepCopyableInterface() || descriptor.annotatedAsDeepCopyableDataClass())
        ) {
            val parameterDeclarations = declaration.primaryConstructorParameters
            descriptor.unsubstitutedPrimaryConstructor
                ?.valueParameters
                ?.forEachIndexed { index, value ->
                    val userType = parameterDeclarations[index].typeReference?.userType()
                    checkType(value.type, context, userType)
                }
        }
    }

    private fun PsiElement.userType() = getChildOfType<KtUserType>()

    private fun checkType(
        type: KotlinType,
        context: DeclarationCheckerContext,
        userType: KtUserType?
    ) {
        if (userType == null) return
        if (KotlinBuiltIns.isPrimitiveTypeOrNullablePrimitiveType(type)) return
        if (KotlinBuiltIns.isString(type)) return
        if (type !is SimpleType) return

        val fqName = type.getJetTypeFqName(false)
        if (fqName in collectionTypes) {
            val typeArgument = userType.typeArguments.single().typeReference?.userType() ?: return
            checkType(type.arguments.single().type, context, typeArgument)
            return
        }

        if (!type.isDeepCopyable()) {
            context.trace.report(
                ErrorsDeepCopy.TYPE_NOT_IMPLEMENT_DEEPCOPYABLE.on(userType, fqName)
            )
        }
    }
}