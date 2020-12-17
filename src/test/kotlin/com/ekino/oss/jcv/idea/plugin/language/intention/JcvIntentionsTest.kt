package com.ekino.oss.jcv.idea.plugin.language.intention

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.ekino.oss.jcv.idea.plugin.language.JcvBasePlatformTestCase
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.json.JsonFileType

/**
 * @see <a href="https://github.com/JetBrains/intellij-community/blob/idea/203.5981.155/json/tests/test/com/intellij/json/JsonIntentionActionTest.java">JsonIntentionActionTest</a>
 */
class JcvIntentionsTest : JcvBasePlatformTestCase() {

  fun `test should suggest jcv replacement on raw json value`() {

    // Given
    val codes = mapOf(
      "should suggest jcv replacement on raw json string value" to """
      {
        "field": "Some<caret> value"
      }
      """.trimIndent(),
      "should suggest jcv replacement on raw json number value" to """
      {
        "field": 2.<caret>3
      }
      """.trimIndent(),
      "should suggest jcv replacement on raw json boolean value" to """
      {
        "field": tru<caret>e
      }
      """.trimIndent(),
      "should suggest jcv replacement on raw json object value" to """
      {
        "field": {<caret>}
      }
      """.trimIndent(),
      "should suggest jcv replacement on raw json array value" to """
      {
        "field": [<caret>]
      }
      """.trimIndent(),
      "should suggest jcv replacement on raw json value inside array" to """
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

  fun `test should not suggest jcv replacement on non raw json value`() {

    // Given
    val codes = mapOf(
      "should not suggest jcv replacement on existing jcv validator" to """
      {
        "field": "{#contains:Some<caret> value#}"
      }
      """.trimIndent(),
      "should not suggest jcv replacement on json property key" to """
      {
        "fie<caret>ld": "Some value"
      }
      """.trimIndent()
    )

    assertAll {
      codes.forEach { (name, code) ->
        // When
        val result = findIntention(code)

        // Then
        assertThat(result, name).isNull()
      }
    }
  }

  private fun findIntention(
    code: String,
    intentionKey: String = "Replace with Jcv validator"
  ): IntentionAction? {
    val psiFile = myFixture.configureByText(JsonFileType.INSTANCE, code)
    myFixture.testHighlighting(true, false, true, psiFile.virtualFile)
    return myFixture.getAvailableIntention(intentionKey)
  }
}
