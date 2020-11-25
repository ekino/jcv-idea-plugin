package com.ekino.oss.jcv.idea_plugin.autocomplete

import com.ekino.oss.jcv.idea_plugin.definition.validators
import com.ekino.oss.jcv.idea_plugin.parsing.Parameter
import com.ekino.oss.jcv.idea_plugin.parsing.TemplateParser
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.json.JsonLanguage
import com.intellij.json.psi.JsonPsiUtil
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.json.psi.JsonValue
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.PostprocessReformattingAspect
import com.intellij.psi.util.parentOfTypes
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.ProcessingContext
import org.jetbrains.annotations.NotNull

class JcvValidatorContributor : CompletionContributor() {

  init {
    extend(
      CompletionType.BASIC,
      PlatformPatterns.psiElement(PsiElement::class.java).withLanguage(JsonLanguage.INSTANCE),
      object : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(
          parameters: CompletionParameters,
          context: ProcessingContext,
          result: CompletionResultSet
        ) {
          @Suppress("MissingRecentApi")
          val originalJsonValue = parameters.originalPosition
            ?.parentOfTypes(JsonValue::class)
            ?.takeIf { JsonPsiUtil.isPropertyValue(it) }
            as? JsonStringLiteral
            ?: return

          validators
            .map { validator ->
              val suggestion = validator.id
                .let {
                  if (validator.parameters.any { param -> param.required }) "$it:" else it
                }
              LookupElementBuilder.create(suggestion)
                .withTypeText(validator.origin.displayName)
                .withInsertHandler(insertHandler(originalJsonValue))
            }
            .also { result.addAllElements(it) }

          val prefix = result.prefixMatcher.prefix

          val currentParamToContrib = TemplateParser.findContentElements(prefix)
            .filterIsInstance<Parameter>()
            .maxBy { it.index }
            ?: return

          validators
            .filter { prefix.startsWith("${it.id}:") }
            .forEach { validator ->
              validator.parameters
                .getOrNull(currentParamToContrib.index)
                ?.let { param ->
                  param
                    .possibleValues
                    .map {
                      LookupElementBuilder.create("${prefix.substring(0, currentParamToContrib.range.startOffset)}$it")
                        .withTailText(param.description)
                        .withTypeText(validator.origin.displayName)
                        .withInsertHandler(insertHandler(originalJsonValue))
                    }
                    .also { result.addAllElements(it) }
                }
            }
        }

        private fun insertHandler(originalElement: PsiElement): (@NotNull InsertionContext, @NotNull LookupElement) -> Unit {
          return { context, item ->
            val replacement = "\"{#${item.lookupString}#}\""
            context.document.replaceString(
              originalElement.startOffset,
              originalElement.endOffset,
              replacement
            )
            PostprocessReformattingAspect.getInstance(context.project).doPostponedFormatting()
            context.editor.caretModel.moveToOffset(originalElement.startOffset + replacement.indexOfLast { it == '#' })
          }
        }
      })
  }
}
