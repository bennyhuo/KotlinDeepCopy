package com.bennyhuo.kotlin.deepcopy.ide.quickfix

import com.bennyhuo.kotlin.deepcopy.ide.isDataClassLike
import com.bennyhuo.kotlin.deepcopy.ide.safeAs
import com.intellij.codeInsight.intention.IntentionAction
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.nj2k.postProcessing.resolve
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtUserType

/**
 * Created by benny.
 */
object DeepCopyQuickFixFactory : KotlinSingleIntentionActionFactory() {
    override fun createAction(diagnostic: Diagnostic): IntentionAction? {
        val ktClass = diagnostic.psiElement.safeAs<KtUserType>()
            ?.referenceExpression?.mainReference?.resolve()?.safeAs<KtClass>()
            ?.takeIf { it.containingFile.isWritable } ?: return null

        return if (ktClass.isData() || ktClass.isDataClassLike()) {
            DeepCopyAnnotationQuickFix(ktClass)
        } else {
            DeepCopyAddSupertypeQuickFix(ktClass)
        }
    }
}
