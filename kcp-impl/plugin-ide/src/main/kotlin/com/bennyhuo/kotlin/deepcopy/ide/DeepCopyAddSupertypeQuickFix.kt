package com.bennyhuo.kotlin.deepcopy.ide

import com.bennyhuo.kotlin.kcp.deepcopy.compiler.DEEP_COPY_INTERFACE_NAME
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.quickfix.KotlinQuickFixAction
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.nj2k.postProcessing.resolve
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

abstract class AbstractDeepCopyQuickFix<T : KtElement>(element: T) : KotlinQuickFixAction<T>(element) {
    protected companion object {
        fun <T : KtElement> T.shortenReferences() = ShortenReferences.DEFAULT.process(this)
    }

    override fun getFamilyName() = text

    abstract fun invoke(ktPsiFactory: KtPsiFactory, element: T)

    final override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val clazz = element ?: return
        val ktPsiFactory = KtPsiFactory(project, markGenerated = true)
        invoke(ktPsiFactory, clazz)
    }

    abstract class AbstractFactory(private val f: Diagnostic.() -> IntentionAction?) : KotlinSingleIntentionActionFactory() {
        companion object {
            inline fun <reified T : KtElement> Diagnostic.findElement() = psiElement.getNonStrictParentOfType<T>()
        }

        override fun createAction(diagnostic: Diagnostic) = f(diagnostic)
    }
}

class DeepCopyAddSupertypeQuickFix(clazz: KtClass) : AbstractDeepCopyQuickFix<KtClass>(clazz) {
    object Factory : AbstractFactory({
        psiElement.safeAs<KtUserType>()
            ?.referenceExpression?.resolve()?.safeAs<KtClass>()
            ?.let(::DeepCopyAddSupertypeQuickFix)
    })

    override fun getText() = KotlinDeepCopyBundle.message("deepcopy.fix.add.deepcopiable.supertype")

    override fun invoke(ktPsiFactory: KtPsiFactory, element: KtClass) {
        val supertypeName = "$DEEP_COPY_INTERFACE_NAME<${element.name}>"
        element.addSuperTypeListEntry(ktPsiFactory.createSuperTypeEntry(supertypeName)).shortenReferences()
    }
}