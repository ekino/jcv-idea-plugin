package com.ekino.oss.jcv.idea.plugin.language.inspection

import com.ekino.oss.jcv.idea.plugin.language.JcvBundle
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvFile
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.TokenType
import com.intellij.psi.util.descendantsOfType
import com.intellij.psi.util.elementType
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset

class JcvWhiteSpacesInspection : JcvInspectionBase() {

  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
    object : PsiElementVisitor() {
      override fun visitElement(element: PsiElement) {

        val whiteSpaces = (element as? JcvFile)
          ?.let { jcvFile ->
            jcvFile.descendantsOfType<PsiElement>().filter { it.elementType == TokenType.WHITE_SPACE }
          }
          ?: return

        whiteSpaces.forEach { whiteSpacesElt ->
          holder.registerProblem(
            whiteSpacesElt,
            JcvBundle.getMessage("jcv.white-spaces.unexpected"),
            object : LocalQuickFix {
              override fun getFamilyName(): String = JcvBundle.getMessage("jcv.white-spaces.unexpected.remove")

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
}
