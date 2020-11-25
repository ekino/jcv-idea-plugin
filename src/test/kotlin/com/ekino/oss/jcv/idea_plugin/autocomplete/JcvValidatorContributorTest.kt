package com.ekino.oss.jcv.idea_plugin.autocomplete

import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.containsOnly
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.intellij.lang.annotations.Language
import org.junit.Test

class JcvValidatorContributorTest : LightPlatformCodeInsightFixture4TestCase() {

  @Test
  fun `should get all validators completion`() {

    // Given
    @Language("JSON")
    val code = """
    {
      "field": ""
    }
    """.trimIndent()

    // When
    val results = completeBasic(code)

    // Then
    assertThat(results.map { it.lookupString }).containsAll(
      "array_type",
      "boolean_type",
      "contains:",
      "date_time_format:",
      "ends_with:",
      "json_array",
      "json_object",
      "mongo_id",
      "not_empty",
      "not_null",
      "object_type",
      "regex:",
      "starts_with:",
      "string_type",
      "templated_url",
      "templated_url_ending:",
      "templated_url_regex:",
      "url",
      "url_ending:",
      "url_regex:",
      "uuid"
    )
  }

  @Test
  fun `should get all 'id' valdiators completion`() {

    // Given
    @Language("JSON")
    val code = """
    {
      "field": "id"
    }
    """.trimIndent()

    // When
    val results = completeBasic(code)

    // Then
    assertThat(results.map { it.lookupString }).containsOnly(
      "mongo_id",
      "uuid"
    )
  }

  @Test
  fun `should get date format parameter values as completion`() {

    // Given
    @Language("JSON")
    val code = """
    {
      "field": "date_time_format:"
    }
    """.trimIndent()

    // When
    val results = completeBasic(code)

    // Then
    assertThat(results.map { it.lookupString }).containsOnly(
      "date_time_format:",
      "date_time_format:basic_iso_date",
      "date_time_format:iso_date",
      "date_time_format:iso_date_time",
      "date_time_format:iso_instant",
      "date_time_format:iso_local_date",
      "date_time_format:iso_local_date_time",
      "date_time_format:iso_local_time",
      "date_time_format:iso_offset_date",
      "date_time_format:iso_offset_date_time",
      "date_time_format:iso_offset_time",
      "date_time_format:iso_ordinal_date",
      "date_time_format:iso_time",
      "date_time_format:iso_week_date",
      "date_time_format:iso_zoned_date_time",
      "date_time_format:rfc_1123_date_time"
    )
  }

  @Test
  fun `should get date format parameter values matching text as completion`() {

    // Given
    @Language("JSON")
    val code = """
    {
      "field": "date_time_format:iso_date_"
    }
    """.trimIndent()

    // When
    val results = completeBasic(code)

    // Then
    assertThat(results.map { it.lookupString }).containsOnly(
      "date_time_format:iso_date_time",
      "date_time_format:iso_local_date_time",
      "date_time_format:iso_offset_date_time",
      "date_time_format:iso_zoned_date_time",
    )
  }

  @Test
  fun `should get date language parameter values keeping previous param as completion`() {

    // Given
    @Language("JSON")
    val code = """
    {
      "field": "date_time_format:iso_local_date_time;fr-"
    }
    """.trimIndent()

    // When
    val results = completeBasic(code)

    // Then
    assertThat(results.map { it.lookupString }).containsOnly(
      "date_time_format:iso_local_date_time;fr-BE",
      "date_time_format:iso_local_date_time;fr-BF",
      "date_time_format:iso_local_date_time;fr-BI",
      "date_time_format:iso_local_date_time;fr-BJ",
      "date_time_format:iso_local_date_time;fr-BL",
      "date_time_format:iso_local_date_time;fr-CA",
      "date_time_format:iso_local_date_time;fr-CD",
      "date_time_format:iso_local_date_time;fr-CF",
      "date_time_format:iso_local_date_time;fr-CG",
      "date_time_format:iso_local_date_time;fr-CH",
      "date_time_format:iso_local_date_time;fr-CI",
      "date_time_format:iso_local_date_time;fr-CM",
      "date_time_format:iso_local_date_time;fr-DJ",
      "date_time_format:iso_local_date_time;fr-DZ",
      "date_time_format:iso_local_date_time;fr-FR",
      "date_time_format:iso_local_date_time;fr-GA",
      "date_time_format:iso_local_date_time;fr-GF",
      "date_time_format:iso_local_date_time;fr-GN",
      "date_time_format:iso_local_date_time;fr-GP",
      "date_time_format:iso_local_date_time;fr-GQ",
      "date_time_format:iso_local_date_time;fr-HT",
      "date_time_format:iso_local_date_time;fr-KM",
      "date_time_format:iso_local_date_time;fr-LU",
      "date_time_format:iso_local_date_time;fr-MA",
      "date_time_format:iso_local_date_time;fr-MC",
      "date_time_format:iso_local_date_time;fr-MF",
      "date_time_format:iso_local_date_time;fr-MG",
      "date_time_format:iso_local_date_time;fr-ML",
      "date_time_format:iso_local_date_time;fr-MQ",
      "date_time_format:iso_local_date_time;fr-MR",
      "date_time_format:iso_local_date_time;fr-MU",
      "date_time_format:iso_local_date_time;fr-NC",
      "date_time_format:iso_local_date_time;fr-NE",
      "date_time_format:iso_local_date_time;fr-PF",
      "date_time_format:iso_local_date_time;fr-PM",
      "date_time_format:iso_local_date_time;fr-RE",
      "date_time_format:iso_local_date_time;fr-RW",
      "date_time_format:iso_local_date_time;fr-SC",
      "date_time_format:iso_local_date_time;fr-SN",
      "date_time_format:iso_local_date_time;fr-SY",
      "date_time_format:iso_local_date_time;fr-TD",
      "date_time_format:iso_local_date_time;fr-TG",
      "date_time_format:iso_local_date_time;fr-TN",
      "date_time_format:iso_local_date_time;fr-VU",
      "date_time_format:iso_local_date_time;fr-WF",
      "date_time_format:iso_local_date_time;fr-YT"
    )
  }

  @Test
  fun `should match completions with existing json value`() {

    // Given
    @Language("JSON")
    val code = """
    {
      "field": "uu7e02d5cc-171b-4d94-8eee-611963b19535"
    }
    """.trimIndent()

    // When
    val results = completeBasic(code, "\"field\": \"uu".let { code.lastIndexOf(it) + it.length })

    // Then
    assertThat(results.map { it.lookupString }).containsOnly(
      "uuid"
    )
  }

  private fun completeBasic(code: String, caretOffset: Int = code.indexOfLast { it == '"' }): List<LookupElement> {
    myFixture.configureByText("test.json", code)
    myFixture.editor.caretModel.moveToOffset(caretOffset)

    return myFixture.completeBasic()?.toList().orEmpty()
  }
}
