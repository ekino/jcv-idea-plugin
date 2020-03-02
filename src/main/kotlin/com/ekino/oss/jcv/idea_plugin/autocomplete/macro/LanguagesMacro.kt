package com.ekino.oss.jcv.idea_plugin.autocomplete.macro

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.template.Expression
import com.intellij.codeInsight.template.ExpressionContext
import com.intellij.codeInsight.template.Macro
import com.intellij.codeInsight.template.TextResult
import java.util.Locale

class LanguagesMacro : Macro() {
  override fun getPresentableName() = "languages()"

  override fun getName() = "languages"

  override fun calculateResult(params: Array<out Expression>, context: ExpressionContext?) =
    TextResult(Locale.FRENCH.toLanguageTag())

  override fun calculateLookupItems(params: Array<out Expression>, context: ExpressionContext?) =
    Locale.getAvailableLocales()
      .map { it.toLanguageTag() }
      .sorted()
      .map { LookupElementBuilder.create(it) }
      .toTypedArray()
}
