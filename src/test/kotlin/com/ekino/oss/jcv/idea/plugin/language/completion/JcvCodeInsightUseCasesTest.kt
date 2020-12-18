package com.ekino.oss.jcv.idea.plugin.language.completion

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import com.ekino.oss.jcv.idea.plugin.language.JcvBasePlatformTestCase
import com.ekino.oss.jcv.idea.plugin.language.JcvFileType
import com.intellij.codeInsight.completion.CompletionType

/**
 * @see <a href="https://jetbrains.org/intellij/sdk/docs/tutorials/writing_tests_for_plugins/parsing_test.html#define-a-parsing-test">3.2. Define a Test</a>
 */
abstract class JcvCodeInsightUseCasesTest : JcvBasePlatformTestCase() {

  fun `test empty validator completion`() {

    // Given
    val code =
      """{#<caret>"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.complete(CompletionType.BASIC, 1)

    // Then
    val results = myFixture.lookupElementStrings?.toList().orEmpty()
    assertThat(results).containsExactlyInAnyOrder(*getValidatorCompletionForEmpty().toTypedArray())
  }

  open fun getValidatorCompletionForEmpty(): List<String> = emptyList()

  fun `test validator completion for id`() {

    // Given
    val code =
      """{#id<caret>"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.complete(CompletionType.BASIC, 1)

    // Then
    val results = myFixture.lookupElementStrings?.toList().orEmpty()
    assertThat(results).containsExactlyInAnyOrder(*getValidatorCompletionForId().toTypedArray())
  }

  open fun getValidatorCompletionForId(): List<String> = emptyList()

  fun `test validator completion for url`() {

    // Given
    val code =
      """{#url<caret>"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.complete(CompletionType.BASIC, 1)

    // Then
    val results = myFixture.lookupElementStrings?.toList().orEmpty()
    assertThat(results).containsExactlyInAnyOrder(*getValidatorCompletionForUrl().toTypedArray())
  }

  open fun getValidatorCompletionForUrl(): List<String> = emptyList()

  fun `test parameter completion unknown param`() {

    // Given
    val code =
      """{#custom_validator:<caret>"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.complete(CompletionType.BASIC, 1)

    // Then
    val results = myFixture.lookupElementStrings?.toList().orEmpty()
    assertThat(results).containsExactlyInAnyOrder(*getValidatorCompletionForUnknownParam().toTypedArray())
  }

  open fun getValidatorCompletionForUnknownParam(): List<String> = emptyList()
}
