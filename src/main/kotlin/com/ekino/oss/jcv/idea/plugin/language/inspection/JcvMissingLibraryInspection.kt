package com.ekino.oss.jcv.idea.plugin.language.inspection

import com.ekino.oss.jcv.idea.plugin.definition.JcvValidatorRegistry
import com.ekino.oss.jcv.idea.plugin.definition.LibraryOrigin
import com.ekino.oss.jcv.idea.plugin.definition.findLibrary
import com.ekino.oss.jcv.idea.plugin.definition.findModule
import com.ekino.oss.jcv.idea.plugin.language.JcvBundle
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvValidator
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor

class JcvMissingLibraryInspection : JcvInspectionBase() {

  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
    object : PsiElementVisitor() {
      @Suppress("ReturnCount")
      override fun visitElement(element: PsiElement) {

        val validatorIdElement = (element as? JcvValidator)
          ?.validatorIdElement
          ?: return
        val validatorDefinition = validatorIdElement
          .text
          ?.takeIf { it.isNotBlank() }
          ?.let { JcvValidatorRegistry.findById(it) }
          ?: return

        val libraryOrigin = validatorDefinition.origin
          .let { it as? LibraryOrigin }
          ?: return
        element.findModule()?.let { libraryOrigin.findLibrary(it) }
          ?.also { return }

        val dependencyPattern = libraryOrigin
          .dependencyPattern()
          ?: JcvBundle.getMessage("jcv.origin.library.dependency.pattern.unknown")

        holder.registerProblem(
          validatorIdElement,
          JcvBundle.getMessage("jcv.origin.library.missing", dependencyPattern),
          object : LocalQuickFix {
            override fun getFamilyName(): String = JcvBundle.getMessage("jcv.origin.library.maven-central.browse")

            override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
              BrowserUtil.browse("""https://search.maven.org/artifact/${dependencyPattern.replace(":", "/")}""")
            }
          }
        )
      }
    }
}
