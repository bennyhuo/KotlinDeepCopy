package com.bennyhuo.kotlin.deepcopy.ide.quickfix

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.DEEP_COPY_INTERFACE_NAME
import com.bennyhuo.kotlin.deepcopy.ide.KotlinDeepCopyBundle
import com.bennyhuo.kotlin.deepcopy.ide.shortenReferences
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.quickfix.KotlinQuickFixAction
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

class DeepCopyAddSupertypeQuickFix(private val ktClass: KtClass) : KotlinQuickFixAction<KtClass>(ktClass) {

    override fun getFamilyName() = text

    override fun getText() = KotlinDeepCopyBundle.message(
        "deepcopy.fix.add.deepcopyable.supertype",
        ktClass.fqName!!.asString()
    )

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val ktPsiFactory = KtPsiFactory(project, markGenerated = true)
        val supertypeName = "$DEEP_COPY_INTERFACE_NAME<${ktClass.fqName}>"
        ktClass.addSuperTypeListEntry(ktPsiFactory.createSuperTypeEntry(supertypeName)).shortenReferences()
    }
}