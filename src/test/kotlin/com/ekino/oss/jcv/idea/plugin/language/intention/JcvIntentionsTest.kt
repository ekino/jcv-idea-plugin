package com.ekino.oss.jcv.idea.plugin.language.intention

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isNotNull
import com.ekino.oss.jcv.idea.plugin.language.JcvBasePlatformTestCase
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.json.JsonFileType

/**
 * @see <a href="https://github.com/JetBrains/intellij-community/blob/idea/203.5981.155/json/tests/test/com/intellij/json/JsonIntentionActionTest.java">JsonIntentionActionTest</a>
 */
class JcvIntentionsTest : JcvBasePlatformTestCase() {

  fun `test should suggest JCV replacement on raw json value`() {

    // Given
    val codes = mapOf(
      "should suggest JCV replacement on raw json string value" to """
      {
        "field": "Some<caret> value"
      }
      """.trimIndent(),
      "should suggest JCV replacement on raw json number value" to """
      {
        "field": 2.<caret>3
      }
      """.trimIndent(),
      "should suggest JCV replacement on raw json boolean value" to """
      {
        "field": tru<caret>e
      }
      """.trimIndent(),
      "should suggest JCV replacement on raw json object value" to """
      {
        "field": {<caret>}
      }
      """.trimIndent(),
      "should suggest JCV replacement on raw json array value" to """
      {
        "field": [<caret>]
      }
      """.trimIndent(),
      "should suggest JCV replacement on raw json value inside array" to """
      {
        "field": ["Some <caret>value"]
      }
      """.trimIndent()
    )

    assertAll {
      codes.forEach { (name, code) ->
        // When
        val result = findIntention(code)

        // Then
        assertThat(result, name).isNotNull()
      }
    }
  }

  private fun findIntention(
    code: String,
    intentionKey: String = "Replace with JCV validator",
  ): IntentionAction? {
    val psiFile = myFixture.configureByText(JsonFileType.INSTANCE, code)
    myFixture.testHighlighting(true, false, true, psiFile.virtualFile)
    return myFixture.getAvailableIntention(intentionKey)
  }
}
