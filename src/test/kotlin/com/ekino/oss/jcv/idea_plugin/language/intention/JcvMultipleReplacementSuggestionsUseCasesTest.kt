package com.ekino.oss.jcv.idea_plugin.language.intention

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import com.intellij.json.psi.JsonElementGenerator

abstract class JcvMultipleReplacementSuggestionsUseCasesTest : JcvReplacementSuggestionsBaseTest() {

  fun `test should suggest replacements for simple text values`() {
    // Given
    val textValues = listOf("Some value", "Some other value")
    val jsonValues = JsonElementGenerator(project)
      .let { generator -> textValues.map { generator.createStringLiteral(it) } }

    // When
    val results = findReplacementSuggestions(module, jsonValues)

    // Then
    assertThat(results).containsExactlyInAnyOrder(*getSuggestionsForSimpleText(textValues).toTypedArray())
  }

  open fun getSuggestionsForSimpleText(textValues: List<String>): List<JcvValidatorSuggestion> = emptyList()

  fun `test should suggest replacements for dates values`() {
    // Given
    val textValues = listOf("10:15:30", "10:15:30+01:00")
    val jsonValues = JsonElementGenerator(project)
      .let { generator -> textValues.map { generator.createStringLiteral(it) } }

    // When
    val results = findReplacementSuggestions(module, jsonValues)

    // Then
    assertThat(results).containsExactlyInAnyOrder(*getSuggestionsForDate(textValues).toTypedArray())
  }

  open fun getSuggestionsForDate(textValues: List<String>): List<JcvValidatorSuggestion> = emptyList()
}
