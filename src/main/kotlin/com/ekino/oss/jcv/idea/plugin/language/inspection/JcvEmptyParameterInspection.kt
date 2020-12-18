package com.ekino.oss.jcv.idea.plugin.language.inspection

import com.ekino.oss.jcv.idea.plugin.language.JcvBundle
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvParameterEntry
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvParameters
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset

class JcvEmptyParameterInspection : JcvInspectionBase() {

  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
    object : PsiElementVisitor() {
      override fun visitElement(element: PsiElement) {
        when {
          element is JcvParameters
            && element.indexedParameters.isEmpty() -> {
            holder.registerProblem(
              element,
              JcvBundle.getMessage("jcv.parameter.empty"),
              removeSeparatorQuickFix()
            )
          }
          element is JcvParameterEntry && element.parameterValue?.text.isNullOrEmpty() -> {
            holder.registerProblem(
              element,
              JcvBundle.getMessage("jcv.parameter.empty"),
              removeSeparatorQuickFix()
            )
          }
          else -> return
        }
      }
    }

  private fun removeSeparatorQuickFix() = object : LocalQuickFix {
    override fun getFamilyName(): String = JcvBundle.getMessage("jcv.parameter.separator.remove")

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
      val descriptorElt = descriptor.psiElement
      val document = PsiDocumentManager.getInstance(project)
        .getDocument(descriptorElt.containingFile)
        ?: return
      document.deleteString(descriptorElt.startOffset, descriptorElt.endOffset)
    }
  }
}
