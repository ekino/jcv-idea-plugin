package com.ekino.oss.jcv.idea.plugin.language.intention

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import com.ekino.oss.jcv.idea.plugin.language.JcvBasePlatformTestCase
import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonBooleanLiteral
import com.intellij.json.psi.JsonElementGenerator
import com.intellij.json.psi.JsonNumberLiteral
import com.intellij.json.psi.JsonObject

abstract class JcvReplacementSuggestionsBaseTest : JcvBasePlatformTestCase() {
  protected fun suggestion(
    category: SuggestionCategory,
    id: String,
    parameters: List<String> = emptyList()
  ) = JcvValidatorSuggestion(
    category = category,
    validatorId = id,
    parameters = parameters
  )
}

abstract class JcvReplacementSuggestionsUseCasesTest : JcvReplacementSuggestionsBaseTest() {

  fun `test should suggest replacements for simple text`() {

    // Given
    val textValue = "Some value"
    val jsonValue = JsonElementGenerator(project).createStringLiteral(textValue)

    // When
    val results = findReplacementSuggestions(module, listOf(jsonValue))

    // Then
    assertThat(results).containsExactlyInAnyOrder(*getSuggestionsForSimpleText(textValue).toTypedArray())
  }

  open fun getSuggestionsForSimpleText(textValue: String): List<JcvValidatorSuggestion> = emptyList()

  fun `test should suggest replacements for uuid`() {

    // Given
    val textValue = "648545e9-bee0-4066-af56-53f82457c822"
    val jsonValue = JsonElementGenerator(project).createStringLiteral(textValue)

    // When
    val results = findReplacementSuggestions(module, listOf(jsonValue))

    // Then
    assertThat(results).containsExactlyInAnyOrder(*getSuggestionsForUuid(textValue).toTypedArray())
  }

  open fun getSuggestionsForUuid(textValue: String): List<JcvValidatorSuggestion> = emptyList()

  fun `test should suggest replacements for mongo id`() {

    // Given
    val textValue = "648545e9bee04066af5653f8"
    val jsonValue = JsonElementGenerator(project).createStringLiteral(textValue)

    // When
    val results = findReplacementSuggestions(module, listOf(jsonValue))

    // Then
    assertThat(results).containsExactlyInAnyOrder(*getSuggestionsForMongoId(textValue).toTypedArray())
  }

  open fun getSuggestionsForMongoId(textValue: String): List<JcvValidatorSuggestion> = emptyList()

  fun `test should suggest replacements for url`() {

    // Given
    val textValue = "http://some.url:9999/path?param"
    val jsonValue = JsonElementGenerator(project).createStringLiteral(textValue)

    // When
    val results = findReplacementSuggestions(module, listOf(jsonValue))

    // Then
    assertThat(results).containsExactlyInAnyOrder(*getSuggestionsForUrl(textValue).toTypedArray())
  }

  open fun getSuggestionsForUrl(textValue: String): List<JcvValidatorSuggestion> = emptyList()

  fun `test should suggest replacement for templated url`() {

    // Given
    val textValue = "http://some.url:9999/path{?param}"
    val jsonValue = JsonElementGenerator(project).createStringLiteral(textValue)

    // When
    val results = findReplacementSuggestions(module, listOf(jsonValue))

    // Then
    assertThat(results).containsExactlyInAnyOrder(*getSuggestionsForTemplatedUrl(textValue).toTypedArray())
  }

  open fun getSuggestionsForTemplatedUrl(textValue: String): List<JcvValidatorSuggestion> = emptyList()

  /**
   * @see <a href="https://ekino.github.io/jcv/documentation/validators.html#predefined-patterns">date_time_format - Predefined patterns</a>
   */
  fun `test should suggest replacements for date`() {

    // Given
    val dates = mapOf(
      "20111203" to
        listOf(
          "basic_iso_date"
        ),
      "2011-12-03" to
        listOf(
          "iso_local_date",
          "iso_date"
        ),
      "2011-12-03+01:00" to
        listOf(
          "iso_offset_date",
          "iso_date"
        ),
      "10:15:30" to
        listOf(
          "iso_local_time",
          "iso_time"
        ),
      "10:15:30+01:00" to
        listOf(
          "iso_offset_time",
          "iso_time"
        ),
      "2011-12-03T10:15:30" to
        listOf(
          "iso_local_date_time",
          "iso_date_time"
        ),
      "2011-12-03T10:15:30+01:00" to
        listOf(
          "iso_offset_date_time",
          "iso_zoned_date_time",
          "iso_date_time"
        ),
      "2011-12-03T10:15:30+01:00[Europe/Paris]" to
        listOf(
          "iso_zoned_date_time",
          "iso_date_time"
        ),
      "2012-337" to
        listOf(
          "iso_ordinal_date"
        ),
      "2012-W48-6" to
        listOf(
          "iso_week_date"
        ),
      "2011-12-03T10:15:30Z" to
        listOf(
          "iso_offset_date_time",
          "iso_zoned_date_time",
          "iso_date_time",
          "iso_instant"
        ),
      "Tue, 3 Jun 2008 11:05:30 GMT" to
        listOf(
          "rfc_1123_date_time"
        )
    )

    assertAll {
      dates.forEach { (textValue, expectedFormats) ->
        val jsonValue = JsonElementGenerator(project).createStringLiteral(textValue)

        // When
        val results = findReplacementSuggestions(module, listOf(jsonValue))

        // Then
        assertThat(results, "should suggest replacements for date '$textValue'").containsExactlyInAnyOrder(
          *getSuggestionsForDate(textValue, expectedFormats).toTypedArray()
        )
      }
    }
  }

  open fun getSuggestionsForDate(
    textValue: String,
    expectedFormats: List<String>
  ): List<JcvValidatorSuggestion> = emptyList()

  fun `test should suggest replacements for number`() {

    // Given
    val textValue = "2.5"
    val jsonValue = JsonElementGenerator(project).createValue<JsonNumberLiteral>(textValue)

    // When
    val results = findReplacementSuggestions(module, listOf(jsonValue))

    // Then
    assertThat(results).containsExactlyInAnyOrder(*getSuggestionsForNumber(textValue).toTypedArray())
  }

  open fun getSuggestionsForNumber(textValue: String): List<JcvValidatorSuggestion> = emptyList()

  fun `test should suggest replacements for boolean`() {

    // Given
    val textValue = "true"
    val jsonValue = JsonElementGenerator(project).createValue<JsonBooleanLiteral>(textValue)

    // When
    val results = findReplacementSuggestions(module, listOf(jsonValue))

    // Then
    assertThat(results).containsExactlyInAnyOrder(*getSuggestionsForBoolean(textValue).toTypedArray())
  }

  open fun getSuggestionsForBoolean(textValue: String): List<JcvValidatorSuggestion> = emptyList()

  fun `test should suggest replacements for object`() {

    // Given
    //language=json
    val textValue =
      """{ "field": "Some value" }"""
    val jsonValue = JsonElementGenerator(project).createValue<JsonObject>(textValue)

    // When
    val results = findReplacementSuggestions(module, listOf(jsonValue))

    // Then
    assertThat(results).containsExactlyInAnyOrder(*getSuggestionsForObject(textValue).toTypedArray())
  }

  open fun getSuggestionsForObject(textValue: String): List<JcvValidatorSuggestion> = emptyList()

  fun `test should suggest replacements for array`() {

    // Given
    //language=json
    val textValue =
      """[ "Some value" ]"""
    val jsonValue = JsonElementGenerator(project).createValue<JsonArray>(textValue)

    // When
    val results = findReplacementSuggestions(module, listOf(jsonValue))

    // Then
    assertThat(results).containsExactlyInAnyOrder(*getSuggestionsForArray(textValue).toTypedArray())
  }

  open fun getSuggestionsForArray(textValue: String): List<JcvValidatorSuggestion> = emptyList()
}
