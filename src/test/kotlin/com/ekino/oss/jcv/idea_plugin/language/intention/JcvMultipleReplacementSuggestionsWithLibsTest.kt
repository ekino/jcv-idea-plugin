package com.ekino.oss.jcv.idea_plugin.language.intention

import com.intellij.openapi.module.Module

class JcvMultipleReplacementSuggestionsWithLibsTest : JcvMultipleReplacementSuggestionsUseCasesTest() {

  override fun configureModule(module: Module) {
    module.addJcvCore()
  }

  override fun getSuggestionsForSimpleText(textValues: List<String>) = run {
    val category = SuggestionCategory.TEXT
    listOf(
      suggestion(category = category, id = "not_empty"),
      suggestion(category = category, id = "not_null"),
      suggestion(category = category, id = "string_type")
    )
  }

  override fun getSuggestionsForDate(textValues: List<String>): List<JcvValidatorSuggestion> = run {
    val textCategory = SuggestionCategory.TEXT
    listOf(
      suggestion(category = textCategory, id = "not_empty"),
      suggestion(category = textCategory, id = "not_null"),
      suggestion(category = textCategory, id = "string_type"),
      suggestion(
        category = SuggestionCategory.DATE,
        id = "date_time_format",
        parameters = listOf("iso_time")
      )
    )
  }
}
