package com.ekino.oss.jcv.idea.plugin.language.completion

import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.containsExactly
import com.ekino.oss.jcv.idea.plugin.language.JcvFileType
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.openapi.module.Module
import org.intellij.lang.annotations.Language

class JcvCodeInsightWithoutLibsTest : JcvCodeInsightUseCasesTest()

class JcvCodeInsightWithJcvCoreTest : JcvCodeInsightUseCasesTest() {

  override fun configureModule(module: Module) {
    module.addJcvCore()
  }

  override fun getValidatorCompletionForEmpty(): List<String> =
    listOf(
      "array_type",
      "boolean_type",
      "contains",
      "date_time_format",
      "ends_with",
      "not_empty",
      "not_null",
      "number_type",
      "object_type",
      "regex",
      "starts_with",
      "string_type",
      "templated_url",
      "templated_url_ending",
      "templated_url_regex",
      "url",
      "url_ending",
      "url_regex",
      "uuid"
    )

  override fun getValidatorCompletionForId(): List<String> =
    listOf(
      "uuid"
    )

  override fun getValidatorCompletionForUrl(): List<String> =
    listOf(
      "url",
      "url_ending",
      "url_regex",
      "templated_url",
      "templated_url_ending",
      "templated_url_regex"
    )

  fun `test date time format pattern completion param with possible values`() {

    // Given
    val code =
      """{#date_time_format:<caret>"""

    myFixture.configureByText(JcvFileType(), code)

    // When
    myFixture.complete(CompletionType.BASIC, 1)

    // Then
    val results = myFixture.lookupElementStrings?.toList().orEmpty()
    assertThat(results)
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

  fun `test date time format language completion param with possible values`() {

    // Given
    @Language("JCV")
    val code =
      """{#date_time_format:iso_instant;<caret>"""

    myFixture.configureByText(JcvFileType(), code)

    // When
    myFixture.complete(CompletionType.BASIC, 1)

    // Then
    val results = myFixture.lookupElementStrings?.toList().orEmpty()
    assertThat(results)
      .containsAll(
        "fr",
        "fr-BE",
        "fr-CA",
        "fr-FR",
        "en",
        "en-GB",
        "en-US"
      )
  }
}

class JcvCodeInsightWithJcvDbCoreTest : JcvCodeInsightUseCasesTest() {

  override fun configureModule(module: Module) {
    module.addJcvDbCore()
  }

  override fun getValidatorCompletionForEmpty(): List<String> =
    listOf(
      "json_array",
      "json_object"
    )
}

class JcvCodeInsightWithJcvDbMongoTest : JcvCodeInsightUseCasesTest() {

  override fun configureModule(module: Module) {
    module.addJcvDbMongo()
  }

  override fun getValidatorCompletionForEmpty(): List<String> =
    listOf(
      "mongo_id"
    )

  override fun getValidatorCompletionForId(): List<String> =
    listOf(
      "mongo_id"
    )
}

class JcvCodeInsightWithAllLibsTest : JcvCodeInsightUseCasesTest() {

  private val suggestionsProviders = listOf(
    JcvCodeInsightWithJcvCoreTest(),
    JcvCodeInsightWithJcvDbCoreTest(),
    JcvCodeInsightWithJcvDbMongoTest()
  )

  override fun configureModule(module: Module) {
    module.addAllJcvLibraries()
  }

  private fun provide(call: (JcvCodeInsightUseCasesTest) -> List<String>): List<String> =
    suggestionsProviders.flatMap { call(it) }

  override fun getValidatorCompletionForEmpty(): List<String> =
    provide { it.getValidatorCompletionForEmpty() }

  override fun getValidatorCompletionForId(): List<String> =
    provide { it.getValidatorCompletionForId() }

  override fun getValidatorCompletionForUrl(): List<String> =
    provide { it.getValidatorCompletionForUrl() }

  override fun getValidatorCompletionForUnknownParam(): List<String> =
    provide { it.getValidatorCompletionForUnknownParam() }
}
