package com.ekino.oss.jcv.idea_plugin.autocomplete.macro

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class DateFormatsMacroTest {

  private val dateFormatMacro = DateFormatMacro()

  @Test
  fun `get name`() {
    // When / Then
    assertThat(dateFormatMacro.name).isEqualTo("dateFormats")
  }

  @Test
  fun `get presentable name`() {
    // When / Then
    assertThat(dateFormatMacro.presentableName).isEqualTo("dateFormats()")
  }

  @Test
  fun `get first result`() {
    // When
    val result = dateFormatMacro.calculateResult(params = emptyArray(), context = null)

    // Then
    assertThat(result.text).isEqualTo("iso_instant")
  }

  @Test
  fun `get lookup items`() {
    // When
    val lookupItems = dateFormatMacro.calculateLookupItems(params = emptyArray(), context = null)

    // Then
    assertThat(lookupItems.map { it.lookupString })
      .containsExactly(
        "basic_iso_date",
        "iso_date",
        "iso_date_time",
        "iso_instant",
        "iso_local_date",
        "iso_local_date_time",
        "iso_local_time",
        "iso_offset_date",
        "iso_offset_date_time",
        "iso_offset_time",
        "iso_ordinal_date",
        "iso_time",
        "iso_week_date",
        "iso_zoned_date_time",
        "rfc_1123_date_time"
      )
  }
}
