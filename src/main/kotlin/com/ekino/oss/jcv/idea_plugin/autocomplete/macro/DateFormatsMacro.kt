package com.ekino.oss.jcv.idea_plugin.autocomplete.macro

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.template.Expression
import com.intellij.codeInsight.template.ExpressionContext
import com.intellij.codeInsight.template.Macro
import com.intellij.codeInsight.template.TextResult

private val DATE_TIME_FORMATTERS = listOf(
  "iso_instant",
  "iso_local_date",
  "iso_offset_date",
  "iso_date",
  "iso_local_time",
  "iso_offset_time",
  "iso_time",
  "iso_local_date_time",
  "iso_offset_date_time",
  "iso_zoned_date_time",
  "iso_date_time",
  "iso_ordinal_date",
  "iso_week_date",
  "basic_iso_date",
  "rfc_1123_date_time"
)

class DateFormatMacro : Macro() {
  override fun getPresentableName() = "dateFormats()"

  override fun getName() = "dateFormats"

  override fun calculateResult(params: Array<out Expression>, context: ExpressionContext?) =
    TextResult(DATE_TIME_FORMATTERS.first())

  override fun calculateLookupItems(params: Array<out Expression>, context: ExpressionContext?) =
    DATE_TIME_FORMATTERS
      .sorted()
      .map { LookupElementBuilder.create(it) }
      .toTypedArray()
}
