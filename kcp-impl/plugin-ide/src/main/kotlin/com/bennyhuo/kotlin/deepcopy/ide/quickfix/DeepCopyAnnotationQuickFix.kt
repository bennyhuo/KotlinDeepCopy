package com.bennyhuo.kotlin.deepcopy.ide.quickfix

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.DEEP_COPY_ANNOTATION_NAME
import com.bennyhuo.kotlin.deepcopy.ide.KotlinDeepCopyBundle
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.base.utils.fqname.getKotlinFqName
import org.jetbrains.kotlin.idea.quickfix.KotlinQuickFixAction
import org.jetbrains.kotlin.idea.util.addAnnotation
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile

class DeepCopyAnnotationQuickFix(private val ktClass: KtClass) :
    KotlinQuickFixAction<KtClass>(ktClass) {

    override fun getFamilyName() = text

    override fun getText(): String {
        return if (ktClass.isData()) {
            KotlinDeepCopyBundle.message("deepcopy.fix.add.annotate", ktClass.getKotlinFqName()!!.asString())
        } else {
            KotlinDeepCopyBundle.message("deepcopy.fix.dataclass", ktClass.getKotlinFqName()!!.asString())
        }
    }

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        if (!ktClass.isData()) {
            ktClass.addModifier(KtModifierKeywordToken.keywordModifier("data"))
        }
        ktClass.addAnnotation(FqName(DEEP_COPY_ANNOTATION_NAME))
    }
}