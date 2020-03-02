package com.ekino.oss.jcv.idea_plugin.autocomplete.macro

import assertk.assertThat
import assertk.assertions.hasSameSizeAs
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.util.Locale

class LanguagesMacroTest {

  private val languagesMacro = LanguagesMacro()

  @Test
  fun `get name`() {
    // When / Then
    assertThat(languagesMacro.name).isEqualTo("languages")
  }

  @Test
  fun `get presentable name`() {
    // When / Then
    assertThat(languagesMacro.presentableName).isEqualTo("languages()")
  }

  @Test
  fun `get first result`() {
    // When
    val result = languagesMacro.calculateResult(params = emptyArray(), context = null)

    // Then
    assertThat(result.text).isEqualTo("fr")
  }

  @Test
  fun `get lookup items`() {
    // When
    val lookupItems = languagesMacro.calculateLookupItems(params = emptyArray(), context = null)

    // Then
    assertThat(lookupItems)
      .hasSameSizeAs(Locale.getAvailableLocales())
  }
}
