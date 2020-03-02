package com.ekino.oss.jcv.idea_plugin.autocomplete.template_generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.io.StringWriter

class TemplateSetGeneratorTest {

  @Test
  fun `write validators as xml template set`() {
    // Given
    val writer = StringWriter()
    val validators = this::class.java.classLoader.getResource("jcv-test-validators.yml")
    val templateGenerator = TemplateSetGenerator(validatorsFile = validators!!, templateWriter = writer)

    // When
    templateGenerator.writeTemplateSet()

    // Then
    val generatedXml = ""
    writer.write(generatedXml)
    assertThat(writer.toString())
      .isEqualTo(
        //language=xml
        """
          <templateSet group="JCV Tests">
            <template name="{#contains" value="{#contains:${'$'}STRING${'$'}${'$'}END${'$'}#}" description="String contains - JCV validator" toReformat="false" toShortenFQNames="true">
              <context>
                <option name="JSON" value="true"/>
                <option name="JSON_PROPERTY_KEYS" value="false"/>
              </context>
              <variable name="STRING" alwaysStopAt="true"/>
            </template>
            <template name="{#date_time_format_localized" value="{#date_time_format:${'$'}DATE_FORMAT${'$'}:${'$'}LANG_TAG${'$'}${'$'}END${'$'}#}" description="Date format with language - JCV validator" toReformat="false" toShortenFQNames="true">
              <context>
                <option name="JSON" value="true"/>
                <option name="JSON_PROPERTY_KEYS" value="false"/>
              </context>
              <variable name="DATE_FORMAT" expression="dateFormats()" alwaysStopAt="true"/>
              <variable name="LANG_TAG" expression="languages()" alwaysStopAt="true"/>
            </template>
            <template name="{#uuid#}" value="{#uuid#}" description="UUID format - JCV validator" toReformat="false" toShortenFQNames="true">
              <context>
                <option name="JSON" value="true"/>
                <option name="JSON_PROPERTY_KEYS" value="false"/>
              </context>
            </template>
          </templateSet>
          
        """.trimIndent()
      )
  }
}
