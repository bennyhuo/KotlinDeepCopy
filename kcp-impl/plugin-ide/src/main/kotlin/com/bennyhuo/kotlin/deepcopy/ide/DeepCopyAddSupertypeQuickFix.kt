package com.bennyhuo.kotlin.deepcopy.ide

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.DEEP_COPY_INTERFACE_NAME
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.quickfix.KotlinQuickFixAction
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.nj2k.postProcessing.resolve
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

class DeepCopyAddSupertypeQuickFix(ktClass: KtClass) : KotlinQuickFixAction<KtClass>(ktClass) {
    object Factory : KotlinSingleIntentionActionFactory() {
        override fun createAction(diagnostic: Diagnostic): IntentionAction? {
            return diagnostic.psiElement.safeAs<KtUserType>()
                ?.referenceExpression?.resolve()?.safeAs<KtClass>()
                ?.takeIf { it.containingFile.isWritable }
                ?.let(::DeepCopyAddSupertypeQuickFix)
        }
    }

    override fun getFamilyName() = text

    override fun getText() = KotlinDeepCopyBundle.message("deepcopy.fix.add.deepcopyable.supertype")

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val ktClass = element ?: return
        val ktPsiFactory = KtPsiFactory(project, markGenerated = true)
        val supertypeName = "$DEEP_COPY_INTERFACE_NAME<${ktClass.fqName}>"
        ktClass.addSuperTypeListEntry(ktPsiFactory.createSuperTypeEntry(supertypeName)).shortenReferences()
    }
}