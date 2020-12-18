package com.ekino.oss.jcv.idea.plugin.language.inspection

import assertk.assertThat
import assertk.assertions.containsExactly
import com.ekino.oss.jcv.idea.plugin.language.JcvBasePlatformTestCase
import com.ekino.oss.jcv.idea.plugin.language.JcvFileType
import com.ekino.oss.jcv.idea.plugin.service.JcvDefinitionsCache

class JcvInspectionsCustomDefinitionsTest : JcvBasePlatformTestCase() {

  //language=json
  override fun getCustomDefinitions(): String =
    """
    {
      "validators": [
        {
          "id": "my_validator",
          "parameters": [
            {
              "description": "the first param",
              "required": true
            },
            {
              "description": "the second param",
              "required": false
            }
          ]
        }
      ]
    }
    """.trimIndent()

  fun `test nothing to report on custom validator with definition`() {

    // Given
    val code =
      """{#my_validator:param 1;param 2#}"""
    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.enableInspections(JcvUnknownValidatorInspection())

    // Then
    myFixture.checkHighlighting(
      true,
      true,
      true,
      false
    )
    assertThat(JcvDefinitionsCache.getAllValidators().map { it.id })
      .containsExactly("my_validator")
  }
}
