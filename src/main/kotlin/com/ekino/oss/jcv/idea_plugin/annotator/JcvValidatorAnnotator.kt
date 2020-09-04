package com.ekino.oss.jcv.idea_plugin.annotator

import com.ekino.oss.jcv.idea_plugin.definition.validatorForId
import com.ekino.oss.jcv.idea_plugin.parsing.Parameter
import com.ekino.oss.jcv.idea_plugin.parsing.ParametersSeparator
import com.ekino.oss.jcv.idea_plugin.parsing.ParametersStartMarker
import com.ekino.oss.jcv.idea_plugin.parsing.TemplateParser
import com.ekino.oss.jcv.idea_plugin.parsing.Validator
import com.ekino.oss.jcv.idea_plugin.parsing.ValidatorEndMarker
import com.ekino.oss.jcv.idea_plugin.parsing.ValidatorId
import com.ekino.oss.jcv.idea_plugin.parsing.ValidatorStartMarker
import com.ekino.oss.jcv.idea_plugin.suggestion.ValueToDefaultValidatorsSuggestion
import com.ekino.oss.jcv.idea_plugin.utils.shiftStart
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.json.psi.JsonPsiUtil
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.json.psi.JsonValue
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.annotations.NotNull

@Suppress("MissingRecentApi")
class JcvValidatorAnnotator : Annotator {

  override fun annotate(element: PsiElement, holder: AnnotationHolder) {
    val jsonElement = (element as? JsonValue)
      ?.takeIf { JsonPsiUtil.isPropertyValue(it) || JsonPsiUtil.isArrayElement(it) }
      ?: return

    val templateParser = (jsonElement as? JsonStringLiteral)
      ?.let { TemplateParser(it.text.removeSurrounding("\"")) }
    val validatorElement = templateParser?.validator()
    val validatorIdElement = templateParser?.validatorId()

    if (validatorElement != null && validatorIdElement != null) {
      annotateJcvValidator(holder, jsonElement, templateParser, validatorElement, validatorIdElement)
    } else {
      suggestionForRawJsonValue(holder, jsonElement)
    }
  }

  private fun annotateJcvValidator(
    holder: AnnotationHolder,
    jsonElement: JsonValue,
    templateParser: TemplateParser,
    validatorElement: Validator,
    validatorIdElement: ValidatorId
  ) {
    templateParser.unexpectedWhitespaces()
      .forEach { whiteSpaces ->
        val range = whiteSpaces.range.adjustToValue(jsonElement)
        holder.newAnnotation(HighlightSeverity.ERROR, "Unexpected whitespaces")
          .range(range)
          .withFix(action(
            familyName = "Remove",
            text = "Remove unexpected whitespaces",
            invoke = { project, editor, _ ->
              WriteCommandAction.runWriteCommandAction(project) {
                editor?.document?.deleteString(range.startOffset, range.endOffset)
              }
            }
          ))
          .create()
      }

    val validatorId = validatorIdElement.value
    val matchingValidator = validatorForId(validatorId)
    val annotationMessage = listOf(
      validatorId,
      matchingValidator?.origin?.displayName ?: "JCV Validator"
    )
      .joinToString(" - ")

    holder.newAnnotation(HighlightSeverity.INFORMATION, annotationMessage)
      .range(validatorElement.range.adjustToValue(jsonElement))
      .create()

    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
      .range(validatorIdElement.range.adjustToValue(jsonElement))
      .textAttributes(DefaultLanguageHighlighterColors.IDENTIFIER)
      .create()

    templateParser.matchingElements()
      .filter { it is ParametersStartMarker || it is ParametersSeparator || it is ValidatorStartMarker || it is ValidatorEndMarker }
      .forEach { marker ->
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
          .range(marker.range.adjustToValue(jsonElement))
          .textAttributes(DefaultLanguageHighlighterColors.KEYWORD)
          .create()
      }

    templateParser.matchingElements()
      .filterIsInstance<Parameter>()
      .forEach { param ->
        val range = param.range.let { if (param.value.isEmpty()) it.shiftStart(-1) else it }

        val annotationBuilder = matchingValidator?.parameters?.getOrNull(param.index)?.description
          ?.let { holder.newAnnotation(HighlightSeverity.INFORMATION, "'$it' parameter") }
          ?: holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
        annotationBuilder
          .range(range.adjustToValue(jsonElement))
          .textAttributes(DefaultLanguageHighlighterColors.STRING)
          .create()
      }

    val foundParameters = templateParser.parameters()
    matchingValidator
      ?.parameters
      .orEmpty()
      .zipAll(foundParameters)
      .forEach { (paramDefinition, foundParam) ->
        when {
          matchingValidator != null && paramDefinition == null && foundParam != null -> {
            val previousCharRange = foundParam.range.adjustToValue(jsonElement).shiftStart(-1)
            holder.newAnnotation(HighlightSeverity.ERROR, "Unexpected parameter for '${matchingValidator.id}'")
              .range(previousCharRange)
              .withFix(action(
                familyName = "Remove",
                text = "Remove unexpected parameter",
                invoke = { project, editor, _ ->
                  WriteCommandAction.runWriteCommandAction(project) {
                    editor?.document?.deleteString(previousCharRange.startOffset, previousCharRange.endOffset)
                  }
                }
              ))
              .create()
          }
          paramDefinition?.required == true && (foundParam == null || foundParam.value.isEmpty()) -> {
            val range = foundParam?.range?.shiftStart(-1) ?: validatorIdElement.range
            holder.newAnnotation(
              HighlightSeverity.ERROR,
              "'${paramDefinition.description}' parameter is required"
            )
              .range(range.adjustToValue(jsonElement))
              .create()
          }
          (paramDefinition == null || !paramDefinition.required) && foundParam?.value?.isEmpty() == true -> {
            val previousCharRange = foundParam.range.adjustToValue(jsonElement).shiftStart(-1)

            holder.newAnnotation(HighlightSeverity.WARNING, "Empty parameter")
              .range(previousCharRange)
              .withFix(action(
                familyName = "Remove",
                text = "Remove separator",
                invoke = { project, editor, _ ->
                  WriteCommandAction.runWriteCommandAction(project) {
                    editor?.document?.deleteString(previousCharRange.startOffset, previousCharRange.endOffset)
                  }
                }
              ))
              .create()
          }
        }
      }
  }

  private fun suggestionForRawJsonValue(
    holder: AnnotationHolder,
    jsonElement: JsonValue
  ) {
    ValueToDefaultValidatorsSuggestion().suggest(jsonElement)
      .takeIf { it.isNotEmpty() }
      ?.also { suggestions ->
        val range = jsonElement.textRange ?: TextRange.EMPTY_RANGE

        val annotationBuilder = holder.newAnnotation(HighlightSeverity.INFORMATION, "JSON value")

        suggestions.forEach { replacement ->
          annotationBuilder
            .newFix(action(
              familyName = "Replace",
              text = "Replace with $replacement",
              invoke = { project, editor, _ ->
                WriteCommandAction.runWriteCommandAction(project) {
                  editor?.document?.replaceString(range.startOffset, range.endOffset, replacement)
                  editor?.caretModel?.moveToOffset(range.startOffset + replacement.indexOfLast { it == '#' })
                }
              }
            ))
            .range(range)
            .registerFix()
        }
        annotationBuilder.create()
      }
  }

  private fun action(
    familyName: String,
    text: String,
    invoke: (project: Project, editor: Editor?, file: PsiFile?) -> Unit
  ) = object : BaseIntentionAction() {
    override fun getFamilyName(): String = familyName

    override fun getText(): String = text

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
      WriteCommandAction.runWriteCommandAction(project) {
        invoke(project, editor, file)
      }
    }
  }

  private fun TextRange.adjustToValue(element: JsonValue): @NotNull TextRange {
    val quoteOffset = (element as? JsonStringLiteral)?.takeIf { it.isQuotedString }?.let { 1 } ?: 0
    return this.shiftRight(element.textRange.startOffset + quoteOffset)
  }
}

fun <T1 : Any, T2 : Any> List<T1>.zipAll(
  other: List<T2>,
  emptyValue: T1? = null,
  otherEmptyValue: T2? = null
): List<Pair<T1?, T2?>> {
  val i1 = this.iterator()
  val i2 = other.iterator()
  return generateSequence {
    if (i1.hasNext() || i2.hasNext()) {
      Pair(
        if (i1.hasNext()) i1.next() else emptyValue,
        if (i2.hasNext()) i2.next() else otherEmptyValue
      )
    } else {
      null
    }
  }.toList()
}

