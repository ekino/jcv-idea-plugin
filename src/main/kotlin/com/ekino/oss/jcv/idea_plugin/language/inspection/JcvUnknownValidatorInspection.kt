package com.ekino.oss.jcv.idea_plugin.language.inspection

import com.ekino.oss.jcv.idea_plugin.definition.JcvValidatorRegistry
import com.ekino.oss.jcv.idea_plugin.definition.custom.JcvValidatorDefinitions
import com.ekino.oss.jcv.idea_plugin.definition.custom.JcvValidatorDescription
import com.ekino.oss.jcv.idea_plugin.definition.custom.JcvValidatorParameterDescription
import com.ekino.oss.jcv.idea_plugin.definition.custom.ValidatorDescriptionsResolver
import com.ekino.oss.jcv.idea_plugin.definition.custom.ValidatorDescriptionsResolver.getOrCreateDefinitionsSource
import com.ekino.oss.jcv.idea_plugin.language.JcvBundle
import com.ekino.oss.jcv.idea_plugin.language.psi.JcvValidator
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor

class JcvUnknownValidatorInspection : JcvInspectionBase() {

  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
    object : PsiElementVisitor() {
      override fun visitElement(element: PsiElement) {

        val validatorIdElement = (element as? JcvValidator)
          ?.validatorIdElement
          ?: return

        val validatorId = validatorIdElement.text
        validatorId
          ?.takeIf { it.isNotBlank() }
          ?.let { JcvValidatorRegistry.findById(it) }
          ?.also { return }

        holder.registerProblem(
          validatorIdElement,
          JcvBundle.getMessage("jcv.validator.unknown"),
          object : LocalQuickFix {
            override fun getFamilyName(): String = JcvBundle.getMessage("jcv.validator.unknown.add")

            override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
              val definitionsFile = getOrCreateDefinitionsSource(project) ?: return
              val existingValidators = ValidatorDescriptionsResolver.parse(definitionsFile)
                ?.validators
                ?: emptyList()

              val newValidators = existingValidators + JcvValidatorDescription(
                id = validatorId,
                parameters = element.parameters
                  ?.indexedParameters
                  ?.map { (index, parameterEntry) ->
                    val humanIndex = index + 1
                    JcvValidatorParameterDescription(
                      description = "Parameter $humanIndex",
                      suggestedValues = listOfNotNull(parameterEntry?.parameterValue?.text?.takeIf { it.isNotEmpty() })
                    )
                  }
                  ?: emptyList()
              )

              ValidatorDescriptionsResolver.write(JcvValidatorDefinitions(newValidators), definitionsFile)
              OpenFileDescriptor(project, definitionsFile).navigate(true)
            }
          }
        )
      }
    }
}
