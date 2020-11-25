package com.ekino.oss.jcv.idea_plugin.parsing

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import com.ekino.oss.jcv.idea_plugin.utils.toTextRange
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class TemplateParserTest {

  companion object {

    private fun case(source: String, vararg elements: ValidatorTemplateElement) = Arguments.of(
      source,
      elements.toList()
    )

    @JvmStatic
    fun fullParsingCases() = listOf(
      case(
        "   {#some_id:param1;param 2;param3\\;and still 3;;  param5  ;param6#} ",
        UnexpectedWhitespaces(
          range = (0 until 3).toTextRange(),
          value = "   "
        ),
        Validator(
          range = (3 until 68).toTextRange(),
          value = "{#some_id:param1;param 2;param3\\;and still 3;;  param5  ;param6#}"
        ),
        ValidatorStartMarker(
          range = (3 until 5).toTextRange(),
          value = "{#"
        ),
        ValidatorContent(
          range = (5 until 66).toTextRange(),
          value = "some_id:param1;param 2;param3\\;and still 3;;  param5  ;param6"
        ),
        ValidatorId(
          range = (5 until 12).toTextRange(),
          value = "some_id"
        ),
        ParametersStartMarker(
          range = (12 until 13).toTextRange(),
          value = ":"
        ),
        Parameters(
          range = (13 until 66).toTextRange(),
          value = "param1;param 2;param3\\;and still 3;;  param5  ;param6"
        ),
        Parameter(
          range = (13 until 19).toTextRange(),
          value = "param1",
          index = 0
        ),
        ParametersSeparator(
          range = (19 until 20).toTextRange(),
          value = ";"
        ),
        Parameter(
          range = (20 until 27).toTextRange(),
          value = "param 2",
          index = 1
        ),
        ParametersSeparator(
          range = (27 until 28).toTextRange(),
          value = ";"
        ),
        Parameter(
          range = (28 until 47).toTextRange(),
          value = "param3\\;and still 3",
          index = 2
        ),
        ParametersSeparator(
          range = (47 until 48).toTextRange(),
          value = ";"
        ),
        Parameter(
          range = (48 until 48).toTextRange(),
          value = "",
          index = 3
        ),
        ParametersSeparator(
          range = (48 until 49).toTextRange(),
          value = ";"
        ),
        Parameter(
          range = (49 until 59).toTextRange(),
          value = "  param5  ",
          index = 4
        ),
        ParametersSeparator(
          range = (59 until 60).toTextRange(),
          value = ";"
        ),
        Parameter(
          range = (60 until 66).toTextRange(),
          value = "param6",
          index = 5
        ),
        ValidatorEndMarker(
          range = (66 until 68).toTextRange(),
          value = "#}"
        ),
        UnexpectedWhitespaces(
          range = (68 until 69).toTextRange(),
          value = " "
        )
      ),
      case(
        "{#id:;#}",
        Validator(
          range = (0 until 8).toTextRange(),
          value = "{#id:;#}"
        ),
        ValidatorStartMarker(
          range = (0 until 2).toTextRange(),
          value = "{#"
        ),
        ValidatorContent(
          range = (2 until 6).toTextRange(),
          value = "id:;"
        ),
        ValidatorId(
          range = (2 until 4).toTextRange(),
          value = "id"
        ),
        ParametersStartMarker(
          range = (4 until 5).toTextRange(),
          value = ":"
        ),
        Parameters(
          range = (5 until 6).toTextRange(),
          value = ";"
        ),
        Parameter(
          range = (5 until 5).toTextRange(),
          value = "",
          index = 0
        ),
        ParametersSeparator(
          range = (5 until 6).toTextRange(),
          value = ";"
        ),
        Parameter(
          range = (6 until 6).toTextRange(),
          value = "",
          index = 1
        ),
        ValidatorEndMarker(
          range = (6 until 8).toTextRange(),
          value = "#}"
        )
      ),
      case(
        "{#id:#}",
        Validator(
          range = (0 until 7).toTextRange(),
          value = "{#id:#}"
        ),
        ValidatorStartMarker(
          range = (0 until 2).toTextRange(),
          value = "{#"
        ),
        ValidatorContent(
          range = (2 until 5).toTextRange(),
          value = "id:"
        ),
        ValidatorId(
          range = (2 until 4).toTextRange(),
          value = "id"
        ),
        ParametersStartMarker(
          range = (4 until 5).toTextRange(),
          value = ":"
        ),
        Parameters(
          range = (5 until 5).toTextRange(),
          value = ""
        ),
        Parameter(
          range = (5 until 5).toTextRange(),
          value = "",
          index = 0
        ),
        ValidatorEndMarker(
          range = (5 until 7).toTextRange(),
          value = "#}"
        )
      )
    )

    @JvmStatic
    fun parametersParsingCases() = listOf(
      case(
        "1;2;3",
        Parameter(
          range = (0 until 1).toTextRange(),
          value = "1",
          index = 0
        ),
        ParametersSeparator(
          range = (1 until 2).toTextRange(),
          value = ";"
        ),
        Parameter(
          range = (2 until 3).toTextRange(),
          value = "2",
          index = 1
        ),
        ParametersSeparator(
          range = (3 until 4).toTextRange(),
          value = ";"
        ),
        Parameter(
          range = (4 until 5).toTextRange(),
          value = "3",
          index = 2
        )
      ),
      case(
        "",
        Parameter(
          range = (0 until 0).toTextRange(),
          value = "",
          index = 0
        )
      ),
      case(
        ";",
        Parameter(
          range = (0 until 0).toTextRange(),
          value = "",
          index = 0
        ),
        ParametersSeparator(
          range = (0 until 1).toTextRange(),
          value = ";"
        ),
        Parameter(
          range = (1 until 1).toTextRange(),
          value = "",
          index = 1
        )
      ),
      case(
        ";\\;;",
        Parameter(
          range = (0 until 0).toTextRange(),
          value = "",
          index = 0
        ),
        ParametersSeparator(
          range = (0 until 1).toTextRange(),
          value = ";"
        ),
        Parameter(
          range = (1 until 3).toTextRange(),
          value = "\\;",
          index = 1
        ),
        ParametersSeparator(
          range = (3 until 4).toTextRange(),
          value = ";"
        ),
        Parameter(
          range = (4 until 4).toTextRange(),
          value = "",
          index = 2
        )
      )
    )
  }

  @ParameterizedTest
  @MethodSource("fullParsingCases")
  fun `should parse source into validator template elements`(source: String, elements: List<ValidatorTemplateElement>) {

    // When
    val results = TemplateParser(source).matchingElements()

    // Then
    assertThat(results).containsExactlyInAnyOrder(*elements.toTypedArray())
  }

  @ParameterizedTest
  @MethodSource("parametersParsingCases")
  fun `should parse parameters`(source: String, elements: List<ValidatorTemplateElement>) {

    // When
    val results = TemplateParser.findParametersElements(source)

    // Then
    assertThat(results).containsExactlyInAnyOrder(*elements.toTypedArray())
  }
}
