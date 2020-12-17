package com.ekino.oss.jcv.idea.plugin.language.inspection

import com.ekino.oss.jcv.idea.plugin.definition.JcvValidatorRegistry
import com.ekino.oss.jcv.idea.plugin.language.JcvBundle
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvParameterEntry
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvParameters
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvValidator
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset

class JcvUnexpectedParameterInspection : JcvInspectionBase() {

  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
    object : PsiElementVisitor() {
      @Suppress("ReturnCount")
      override fun visitElement(element: PsiElement) {

        val parameterIndex = when {
          element is JcvParameterEntry -> element.index
          element is JcvParameters && element.indexedParameters.isEmpty() -> 0
          else -> null
        }
          ?: return

        val jcvValidator = element.parentOfType<JcvValidator>() ?: return
        val validatorDefinition = jcvValidator.validatorId?.let { JcvValidatorRegistry.findById(it) } ?: return
        validatorDefinition
          .parameters
          .getOrNull(parameterIndex)
          ?.also { return }

        val targetElement = element
          .takeIf { it is JcvParameterEntry && parameterIndex == 0 }
          ?.parentOfType<JcvParameters>()
          ?.takeIf { it.indexedParameters.size == 1 }
          ?: element

        holder.registerProblem(
          targetElement,
          JcvBundle.getMessage("jcv.parameter.unexpected"),
          object : LocalQuickFix {
            override fun getFamilyName(): String = JcvBundle.getMessage("jcv.parameter.unexpected.remove")

            override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
              val descriptorElt = descriptor.psiElement
              val document = PsiDocumentManager.getInstance(project)
                .getDocument(descriptorElt.containingFile)
                ?: return
              document.deleteString(descriptorElt.startOffset, descriptorElt.endOffset)
            }
          }
        )
      }
    }
}
