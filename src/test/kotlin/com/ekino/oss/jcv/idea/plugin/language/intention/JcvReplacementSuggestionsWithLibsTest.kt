package com.ekino.oss.jcv.idea.plugin.language.intention

import com.intellij.openapi.module.Module

class JcvReplacementSuggestionsWithoutLibsTest : JcvReplacementSuggestionsUseCasesTest()

class JcvReplacementSuggestionsWithJcvCoreTest : JcvReplacementSuggestionsUseCasesTest() {

  override fun configureModule(module: Module) {
    module.addJcvCore()
  }

  override fun getSuggestionsForSimpleText(textValue: String): List<JcvValidatorSuggestion> =
    defaultTextSuggestions(textValue)

  override fun getSuggestionsForUuid(textValue: String): List<JcvValidatorSuggestion> =
    defaultTextSuggestions(textValue) +
      suggestion(category = SuggestionCategory.ID, id = "uuid")

  override fun getSuggestionsForMongoId(textValue: String): List<JcvValidatorSuggestion> =
    defaultTextSuggestions(textValue)

  override fun getSuggestionsForUrl(textValue: String): List<JcvValidatorSuggestion> =
    defaultTextSuggestions(textValue) +
      defaultUrlSuggestions(textValue)

  override fun getSuggestionsForTemplatedUrl(textValue: String): List<JcvValidatorSuggestion> =
    defaultTextSuggestions(textValue) +
      defaultTemplatedUrlSuggestions(textValue)

  override fun getSuggestionsForDate(textValue: String, expectedFormats: List<String>): List<JcvValidatorSuggestion> =
    defaultTextSuggestions(textValue) +
      expectedFormats.map { format ->
        suggestion(category = SuggestionCategory.DATE, id = "date_time_format", parameters = listOf(format))
      }

  override fun getSuggestionsForNumber(textValue: String): List<JcvValidatorSuggestion> =
    listOf(
      suggestion(category = SuggestionCategory.NUMBER, id = "number_type")
    )

  override fun getSuggestionsForBoolean(textValue: String): List<JcvValidatorSuggestion> =
    listOf(
      suggestion(category = SuggestionCategory.BOOLEAN, id = "boolean_type")
    )

  override fun getSuggestionsForObject(textValue: String): List<JcvValidatorSuggestion> =
    listOf(
      suggestion(category = SuggestionCategory.OBJECT, id = "object_type")
    )

  override fun getSuggestionsForArray(textValue: String): List<JcvValidatorSuggestion> =
    listOf(
      suggestion(category = SuggestionCategory.ARRAY, id = "array_type")
    )

  private fun defaultTextSuggestions(parameterValue: String) = run {
    val category = SuggestionCategory.TEXT
    val defaultParameters = listOf(parameterValue)
    listOf(
      suggestion(category = category, id = "starts_with", parameters = defaultParameters),
      suggestion(category = category, id = "ends_with", parameters = defaultParameters),
      suggestion(category = category, id = "contains", parameters = defaultParameters),
      suggestion(category = category, id = "regex", parameters = listOf("\\\\Q$parameterValue\\\\E")),
      suggestion(category = category, id = "not_empty"),
      suggestion(category = category, id = "not_null"),
      suggestion(category = category, id = "string_type")
    )
  }

  private fun defaultUrlSuggestions(@Suppress("SameParameterValue") parameterValue: String) = run {
    val category = SuggestionCategory.URL
    val defaultParameters = listOf(parameterValue)
    listOf(
      suggestion(category = category, id = "url"),
      suggestion(category = category, id = "url_ending", parameters = defaultParameters),
      suggestion(category = category, id = "url_regex", parameters = listOf("\\\\Q$parameterValue\\\\E"))
    )
  }

  private fun defaultTemplatedUrlSuggestions(@Suppress("SameParameterValue") parameterValue: String) = run {
    val category = SuggestionCategory.URL
    val defaultParameters = listOf(parameterValue)
    listOf(
      suggestion(category = category, id = "templated_url"),
      suggestion(category = category, id = "templated_url_ending", parameters = defaultParameters),
      suggestion(category = category, id = "templated_url_regex", parameters = listOf("\\\\Q$parameterValue\\\\E"))
    )
  }
}

class JcvReplacementSuggestionsWithJcvDbCoreTest : JcvReplacementSuggestionsUseCasesTest() {

  override fun configureModule(module: Module) {
    module.addJcvDbCore()
  }

  override fun getSuggestionsForObject(textValue: String): List<JcvValidatorSuggestion> =
    listOf(
      suggestion(category = SuggestionCategory.OBJECT, id = "json_object")
    )

  override fun getSuggestionsForArray(textValue: String): List<JcvValidatorSuggestion> =
    listOf(
      suggestion(category = SuggestionCategory.ARRAY, id = "json_array")
    )
}

class JcvReplacementSuggestionsWithJcvDbMongoTest : JcvReplacementSuggestionsUseCasesTest() {

  override fun configureModule(module: Module) {
    module.addJcvDbMongo()
  }

  override fun getSuggestionsForMongoId(textValue: String): List<JcvValidatorSuggestion> =
    listOf(
      suggestion(category = SuggestionCategory.ID, id = "mongo_id")
    )
}

class JcvReplacementSuggestionsWithAllLibsTest : JcvReplacementSuggestionsUseCasesTest() {

  private val suggestionsProviders = listOf(
    JcvReplacementSuggestionsWithJcvCoreTest(),
    JcvReplacementSuggestionsWithJcvDbCoreTest(),
    JcvReplacementSuggestionsWithJcvDbMongoTest()
  )

  override fun configureModule(module: Module) {
    module.addAllJcvLibraries()
  }

  private fun provide(call: (JcvReplacementSuggestionsUseCasesTest) -> List<JcvValidatorSuggestion>): List<JcvValidatorSuggestion> =
    suggestionsProviders.flatMap { call(it) }

  override fun getSuggestionsForSimpleText(textValue: String): List<JcvValidatorSuggestion> =
    provide { it.getSuggestionsForSimpleText(textValue) }

  override fun getSuggestionsForUuid(textValue: String): List<JcvValidatorSuggestion> =
    provide { it.getSuggestionsForUuid(textValue) }

  override fun getSuggestionsForMongoId(textValue: String): List<JcvValidatorSuggestion> =
    provide { it.getSuggestionsForMongoId(textValue) }

  override fun getSuggestionsForUrl(textValue: String): List<JcvValidatorSuggestion> =
    provide { it.getSuggestionsForUrl(textValue) }

  override fun getSuggestionsForTemplatedUrl(textValue: String): List<JcvValidatorSuggestion> =
    provide { it.getSuggestionsForTemplatedUrl(textValue) }

  override fun getSuggestionsForDate(textValue: String, expectedFormats: List<String>): List<JcvValidatorSuggestion> =
    provide { it.getSuggestionsForDate(textValue, expectedFormats) }

  override fun getSuggestionsForNumber(textValue: String): List<JcvValidatorSuggestion> =
    provide { it.getSuggestionsForNumber(textValue) }

  override fun getSuggestionsForBoolean(textValue: String): List<JcvValidatorSuggestion> =
    provide { it.getSuggestionsForBoolean(textValue) }

  override fun getSuggestionsForObject(textValue: String): List<JcvValidatorSuggestion> =
    provide { it.getSuggestionsForObject(textValue) }

  override fun getSuggestionsForArray(textValue: String): List<JcvValidatorSuggestion> =
    provide { it.getSuggestionsForArray(textValue) }
}
