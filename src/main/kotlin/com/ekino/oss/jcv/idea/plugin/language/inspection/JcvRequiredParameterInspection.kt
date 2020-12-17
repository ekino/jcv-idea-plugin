package com.ekino.oss.jcv.idea.plugin.language.inspection

import com.ekino.oss.jcv.idea.plugin.definition.JcvValidatorRegistry
import com.ekino.oss.jcv.idea.plugin.language.JcvBundle
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvValidator
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor

class JcvRequiredParameterInspection : JcvInspectionBase() {

  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
    object : PsiElementVisitor() {
      override fun visitElement(element: PsiElement) {

        val jcvValidator = element as? JcvValidator ?: return

        val validatorDefinition = jcvValidator.validatorId?.let { JcvValidatorRegistry.findById(it) } ?: return

        val parametersElt = jcvValidator.parameters
        validatorDefinition
          .parameters
          .mapIndexedNotNull { index, paramDef ->
            paramDef.takeIf { it.required }
              ?.let { it to parametersElt?.indexedParameters?.getOrNull(index)?.second }
          }
          .forEach { (paramDef, paramEntry) ->
            val targetElt = when {
              paramEntry != null -> paramEntry.takeIf { it.parameterValue?.text.isNullOrEmpty() }
              jcvValidator.parameters != null -> jcvValidator.parameters
              else -> jcvValidator.validatorIdElement
            }
              ?: return@forEach
            holder.registerProblem(
              targetElt,
              JcvBundle.getMessage("jcv.parameter.required", paramDef.description)
            )
          }
      }
    }
}
