package com.ekino.oss.jcv.idea_plugin.annotator

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.containsAll
import com.ekino.oss.jcv.idea_plugin.utils.toTextRange
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.json.JsonFileType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.intellij.lang.annotations.Language
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.params.provider.Arguments

class JcvValidatorAnnotatorTest : BasePlatformTestCase() {

  companion object {
    @JvmStatic
    fun dateForFormatProvider() = listOf(
      Arguments.of(
        "20111203",
        listOf(
          "basic_iso_date"
        )
      ),
      Arguments.of(
        "2011-12-03",
        listOf(
          "iso_local_date",
          "iso_date"
        )
      ),
      Arguments.of(
        "2011-12-03+01:00",
        listOf(
          "iso_offset_date"
        )
      ),
      Arguments.of(
        "10:15:30",
        listOf(
          "iso_local_time",
          "iso_time"
        )
      ),
      Arguments.of(
        "10:15:30+01:00",
        listOf(
          "iso_offset_time"
        )
      ),
      Arguments.of(
        "2011-12-03T10:15:30",
        listOf(
          "iso_local_date_time"
        )
      ),
      Arguments.of(
        "2011-12-03T10:15:30+01:00",
        listOf(
          "iso_offset_date_time"
        )
      ),
      Arguments.of(
        "2011-12-03T10:15:30+01:00[Europe/Paris]",
        listOf(
          "iso_zoned_date_time",
          "iso_date_time"
        )
      ),
      Arguments.of(
        "2012-337",
        listOf(
          "iso_ordinal_date"
        )
      ),
      Arguments.of(
        "2012-W48-6",
        listOf(
          "iso_week_date"
        )
      ),
      Arguments.of(
        "2011-12-03T10:15:30Z",
        listOf(
          "iso_instant"
        )
      ),
      Arguments.of(
        "Tue, 3 Jun 2008 11:05:30 GMT",
        listOf(
          "rfc_1123_date_time"
        )
      )
    )
  }

  fun `test should find validators in json value`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "some_validator": "{#some_validator#}"
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "{#",
        textAttribute = "DEFAULT_KEYWORD"
      ),
      HighlightAssertionData(
        text = "{#some_validator#}",
        description = "some_validator - JCV Validator"
      ),
      HighlightAssertionData(
        text = "some_validator",
        textAttribute = "DEFAULT_IDENTIFIER"
      ),
      HighlightAssertionData(
        text = "#}",
        textAttribute = "DEFAULT_KEYWORD"
      )
    )
  }

  fun `test should find core validator with params in json value`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "some_validator": "{#date_time_format:iso_instant;fr#}"
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "{#",
        textAttribute = "DEFAULT_KEYWORD"
      ),
      HighlightAssertionData(
        text = "{#date_time_format:iso_instant;fr#}",
        description = "date_time_format - JCV Core"
      ),
      HighlightAssertionData(
        text = "date_time_format",
        textAttribute = "DEFAULT_IDENTIFIER"
      ),
      HighlightAssertionData(
        text = ":",
        textAttribute = "DEFAULT_KEYWORD"
      ),
      HighlightAssertionData(
        text = "iso_instant",
        description = "'the date time pattern' parameter",
        textAttribute = "DEFAULT_STRING"
      ),
      HighlightAssertionData(
        text = ";",
        textAttribute = "DEFAULT_KEYWORD"
      ),
      HighlightAssertionData(
        text = "fr",
        description = "'the language tag' parameter",
        textAttribute = "DEFAULT_STRING"
      ),
      HighlightAssertionData(
        text = "#}",
        textAttribute = "DEFAULT_KEYWORD"
      )
    )
  }

  fun `test should find invalid whitespaces in json value`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "some_validator": "  {#some_validator#} "
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "  ",
        description = "Unexpected whitespaces",
        severity = HighlightSeverity.ERROR,
        textAttribute = "ERRORS_ATTRIBUTES"
      ),
      HighlightAssertionData(
        text = "{#",
        textAttribute = "DEFAULT_KEYWORD"
      ),
      HighlightAssertionData(
        text = "{#some_validator#}",
        description = "some_validator - JCV Validator"
      ),
      HighlightAssertionData(
        text = "some_validator",
        textAttribute = "DEFAULT_IDENTIFIER"
      ),
      HighlightAssertionData(
        text = "#}",
        textAttribute = "DEFAULT_KEYWORD"
      ),
      HighlightAssertionData(
        text = " ",
        description = "Unexpected whitespaces",
        severity = HighlightSeverity.ERROR,
        textAttribute = "ERRORS_ATTRIBUTES"
      )
    )

    assertThat(results.extractQuickFixes())
      .containsAll(
        quickFix("Remove unexpected whitespaces", 23 until 25),
        quickFix("Remove unexpected whitespaces", 43 until 44)
      )
  }

  fun `test should find empty params in json value`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "some_validator": "{#some_validator:;#}"
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "{#",
        textAttribute = "DEFAULT_KEYWORD"
      ),
      HighlightAssertionData(
        text = "{#some_validator:;#}",
        description = "some_validator - JCV Validator"
      ),
      HighlightAssertionData(
        text = "some_validator",
        textAttribute = "DEFAULT_IDENTIFIER"
      ),
      HighlightAssertionData(
        text = ":",
        description = "Empty parameter",
        severity = HighlightSeverity.WARNING,
        textAttribute = "WARNING_ATTRIBUTES"
      ),
      HighlightAssertionData(
        text = ":",
        textAttribute = "DEFAULT_KEYWORD"
      ),
      HighlightAssertionData(
        text = ";",
        description = "Empty parameter",
        severity = HighlightSeverity.WARNING,
        textAttribute = "WARNING_ATTRIBUTES"
      ),
      HighlightAssertionData(
        text = ";",
        textAttribute = "DEFAULT_KEYWORD"
      ),
      HighlightAssertionData(
        text = "#}",
        textAttribute = "DEFAULT_KEYWORD"
      )
    )

    assertThat(results.extractQuickFixes())
      .containsAll(
        quickFix("Remove separator", 39 until 40),
        quickFix("Remove separator", 40 until 41)
      )
  }

  fun `test should find required param for known validator`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "some_validator": "{#contains:#}"
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "{#",
        textAttribute = "DEFAULT_KEYWORD"
      ),
      HighlightAssertionData(
        text = "{#contains:#}",
        description = "contains - JCV Core"
      ),
      HighlightAssertionData(
        text = "contains",
        textAttribute = "DEFAULT_IDENTIFIER"
      ),
      HighlightAssertionData(
        text = ":",
        description = "'the text to search for' parameter is required",
        severity = HighlightSeverity.ERROR,
        textAttribute = "ERRORS_ATTRIBUTES"
      ),
      HighlightAssertionData(
        text = "#}",
        textAttribute = "DEFAULT_KEYWORD"
      )
    )
  }

  fun `test should find unexpected param for known validator`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "some_validator": "{#contains:test;#}"
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "{#",
        textAttribute = "DEFAULT_KEYWORD"
      ),
      HighlightAssertionData(
        text = "{#contains:test;#}",
        description = "contains - JCV Core"
      ),
      HighlightAssertionData(
        text = "contains",
        textAttribute = "DEFAULT_IDENTIFIER"
      ),
      HighlightAssertionData(
        text = ":",
        textAttribute = "DEFAULT_KEYWORD"
      ),
      HighlightAssertionData(
        text = "test",
        description = "'the text to search for' parameter",
        textAttribute = "DEFAULT_STRING"
      ),
      HighlightAssertionData(
        text = ";",
        description = "Unexpected parameter for 'contains'",
        severity = HighlightSeverity.ERROR,
        textAttribute = "ERRORS_ATTRIBUTES"
      ),
      HighlightAssertionData(
        text = "#}",
        textAttribute = "DEFAULT_KEYWORD"
      )
    )

    assertThat(results.extractQuickFixes())
      .containsAll(
        quickFix("Remove unexpected parameter", 38 until 39)
      )
  }

  fun `test should get suggestion for boolean json value`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "field": true
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "true",
        description = "JSON value"
      )
    )

    assertThat(results.extractQuickFixes())
      .containsAll(
        quickFix("Replace with \"{#boolean_type#}\"", 13 until 17)
      )
  }

  fun `test should get suggestion for number json value`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "field": 12.0
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "12.0",
        description = "JSON value"
      )
    )

    assertThat(results.extractQuickFixes())
      .containsAll(
        quickFix("Replace with \"{#number_type#}\"", 13 until 17)
      )
  }

  fun `test should get suggestion for object json value`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "field": {
      
      }
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "{\n  \n  }",
        description = "JSON value"
      )
    )

    val range = 13 until 21
    assertThat(results.extractQuickFixes())
      .containsAll(
        quickFix("Replace with \"{#object_type#}\"", range),
        quickFix("Replace with \"{#json_object#}\"", range)
      )
  }

  fun `test should get suggestion for array json value`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "field": [
      
      ]
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "[\n  \n  ]",
        description = "JSON value"
      )
    )

    val range = 13 until 21
    assertThat(results.extractQuickFixes())
      .containsAll(
        quickFix("Replace with \"{#array_type#}\"", range),
        quickFix("Replace with \"{#json_array#}\"", range)
      )
  }

  fun `test should get suggestion for text json value`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "field": "Some value"
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "\"Some value\"",
        description = "JSON value"
      )
    )

    assertThat(results.extractQuickFixes())
      .containsAll(
        *"Some value".generateTextQuickFixes(13 until 25).toTypedArray()
      )
  }

  fun `test should get suggestion for uuid text json value`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "field": "cd81ebf3-1e88-48d6-9788-38b17805c525"
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "\"cd81ebf3-1e88-48d6-9788-38b17805c525\"",
        description = "JSON value"
      )
    )

    val range = 13 until 51
    assertThat(results.extractQuickFixes())
      .containsAll(
        *"cd81ebf3-1e88-48d6-9788-38b17805c525".generateTextQuickFixes(range).toTypedArray(),
        quickFix("Replace with \"{#uuid#}\"", range),
      )
  }

  fun `test should get suggestion for mongo id text json value`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "field": "cd81ebf31e8848d6978838b1"
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "\"cd81ebf31e8848d6978838b1\"",
        description = "JSON value"
      )
    )

    val range = 13 until 39
    assertThat(results.extractQuickFixes())
      .containsAll(
        *"cd81ebf31e8848d6978838b1".generateTextQuickFixes(range).toTypedArray(),
        quickFix("Replace with \"{#mongo_id#}\"", range),
      )
  }

  fun `test should get suggestion for url text json value`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "field": "http://localhost:8080/some/url"
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "\"http://localhost:8080/some/url\"",
        description = "JSON value"
      )
    )

    val range = 13 until 45
    val text = "http://localhost:8080/some/url"
    assertThat(results.extractQuickFixes())
      .containsAll(
        *text.generateTextQuickFixes(range).toTypedArray(),
        quickFix("Replace with \"{#url#}\"", range),
        quickFix("Replace with \"{#url_ending:$text#}\"", range),
        quickFix("Replace with \"{#url_regex:\\\\Q$text\\\\E#}\"", range),
      )
  }

  fun `test should get suggestion for templated url text json value`() {
    // Given
    @Language("JSON")
    val code = """
    {
      "field": "http://localhost:8080/some/url{?param}"
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "\"http://localhost:8080/some/url{?param}\"",
        description = "JSON value"
      )
    )

    val range = 13 until 53
    val text = "http://localhost:8080/some/url{?param}"
    assertThat(results.extractQuickFixes())
      .containsAll(
        *text.generateTextQuickFixes(range).toTypedArray(),
        quickFix("Replace with \"{#templated_url#}\"", range),
        quickFix("Replace with \"{#templated_url_ending:$text#}\"", range),
        quickFix("Replace with \"{#templated_url_regex:\\\\Q$text\\\\E#}\"", range),
      )
  }

  fun `test should get suggestion for date text json value`() {
    assertAll {
      dateForFormatProvider()
        .forEach {
          val args = it.get()
          @Suppress("UNCHECKED_CAST")
          assertDateQuickFixes(args[0] as String, args[1] as List<String>)
        }
    }
  }

  private fun assertDateQuickFixes(fieldValue: String, formats: List<String>) {
    // Given
    @Language("JSON")
    val code = """
    {
      "field": "$fieldValue"
    }
    """.trimIndent()

    // When
    val results = doHighlighting(code)

    // Then
    assertContainsAllHighlights(
      results,
      HighlightAssertionData(
        text = "\"$fieldValue\"",
        description = "JSON value"
      )
    )

    val range = 13.let { it until (it + fieldValue.length + 2) }
    val dateQuickFixes = formats.map { quickFix("Replace with \"{#date_time_format:$it#}\"", range) }
    assertThat(results.extractQuickFixes())
      .containsAll(
        *fieldValue.generateTextQuickFixes(range).toTypedArray(),
        *dateQuickFixes.toTypedArray()
      )
  }

  private fun String.generateTextQuickFixes(range: IntRange) = listOf(
    "Replace with \"{#starts_with:$this#}\"",
    "Replace with \"{#ends_with:$this#}\"",
    "Replace with \"{#contains:$this#}\"",
    "Replace with \"{#regex:\\\\Q$this\\\\E#}\"",
    "Replace with \"{#not_empty#}\"",
    "Replace with \"{#not_null#}\"",
    "Replace with \"{#string_type#}\""
  )
    .map { quickFix(it, range) }

  private fun doHighlighting(code: String): @NotNull MutableList<HighlightInfo> {
    myFixture.configureByText(JsonFileType.INSTANCE, code)
    PsiDocumentManager.getInstance(project).commitAllDocuments()

    return myFixture.doHighlighting()
  }

  private fun assertContainsAllHighlights(results: List<HighlightInfo>, vararg assertionData: HighlightAssertionData) {
    val transformedResults = results.map {
      HighlightAssertionData(
        text = it.text,
        description = it.description,
        severity = it.severity,
        textAttribute = it.forcedTextAttributesKey?.externalName
      )
    }
    assertThat(transformedResults)
      .containsAll(*assertionData)
  }

  private fun List<HighlightInfo>.extractQuickFixes() = this
    .flatMap { it.quickFixActionRanges.orEmpty() }
    .map { it.first.action.text to it.second }

  private fun quickFix(text: String, range: IntRange) = text to range.toTextRange()
}

private data class HighlightAssertionData(
  val text: String,
  val description: String? = null,
  val severity: HighlightSeverity = HighlightSeverity.INFORMATION,
  val textAttribute: String? = null
)
