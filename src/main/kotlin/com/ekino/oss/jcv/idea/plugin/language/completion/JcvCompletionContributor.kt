package com.ekino.oss.jcv.idea.plugin.language.completion

import com.ekino.oss.jcv.idea.plugin.definition.JcvValidatorDefinition
import com.ekino.oss.jcv.idea.plugin.definition.JcvValidatorRegistry
import com.ekino.oss.jcv.idea.plugin.definition.existsInModule
import com.ekino.oss.jcv.idea.plugin.definition.findModule
import com.ekino.oss.jcv.idea.plugin.language.JcvUtil
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvTypes
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvValidator
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext

class JcvCompletionContributor : CompletionContributor() {
  init {
    extend(
      CompletionType.BASIC,
      PlatformPatterns.psiElement(JcvTypes.VALIDATOR_ID),
      object : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(
          parameters: CompletionParameters,
          context: ProcessingContext,
          result: CompletionResultSet
        ) {
          val existsInModulePredicate: (JcvValidatorDefinition) -> Boolean = parameters.position.findModule()
            ?.let { module -> { it.existsInModule(module) } }
            ?: { true }
          JcvValidatorRegistry.getAllValidators()
            .filter(existsInModulePredicate)
            .map { validatorDefinition ->
              LookupElementBuilder.create(validatorDefinition.id)
                .withTypeText(validatorDefinition.origin.displayName)
                .withIcon(AllIcons.Nodes.Method)
            }
            .also { result.addAllElements(it) }
        }
      }
    )
    extend(
      CompletionType.BASIC,
      PlatformPatterns.psiElement(JcvTypes.PARAMETER),
      object : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(
          parameters: CompletionParameters,
          context: ProcessingContext,
          result: CompletionResultSet
        ) {
          val parameter = parameters.position
          val parameterIndex = JcvUtil.getParameterEntry(parameter)?.index ?: return
          val jcvValidator = parameter.parentOfType<JcvValidator>() ?: return

          jcvValidator.validatorId
            ?.let { JcvValidatorRegistry.findById(it) }
            ?.let { validatorDefinition ->
              validatorDefinition.parameters
                .getOrNull(parameterIndex)
                ?.let { paramDefinition ->
                  paramDefinition.suggestedValues
                    .map { suggestedValue ->
                      LookupElementBuilder.create(suggestedValue)
                        .withTailText(" ${paramDefinition.description}")
                        .withTypeText(validatorDefinition.origin.displayName)
                        .withIcon(AllIcons.Nodes.Parameter)
                    }
                }
                ?.also { result.addAllElements(it) }
            }
        }
      }
    )
  }
}
