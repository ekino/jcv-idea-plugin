package com.ekino.oss.jcv.idea.plugin.language.inspection

import com.ekino.oss.jcv.idea.plugin.language.HighlightingMessage
import com.ekino.oss.jcv.idea.plugin.language.JcvBasePlatformTestCase
import com.ekino.oss.jcv.idea.plugin.language.JcvFileType

class JcvInspectionsTest : JcvBasePlatformTestCase() {

  fun `test unexpected white spaces error`() {

    // Given
    val code =
      """{#my_validator#}"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.enableInspections(JcvWhiteSpacesInspection())

    // Then
    myFixture.checkHighlighting(
      true,
      true,
      true,
      false
    )
  }

  fun `test empty parameter on each separator`() {

    // Given
    val warning = HighlightingMessage.warning("Empty parameter")

    val code =
      """{#my_validator:param 1${warning.wrap(";")};param3${warning.wrap(";")}#}"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.enableInspections(JcvEmptyParameterInspection())

    // Then
    myFixture.checkHighlighting(
      true,
      true,
      true,
      false
    )
  }

  fun `test empty parameters on parameter start marker`() {

    // Given
    val warning = HighlightingMessage.warning("Empty parameter")

    val code =
      """{#my_validator${warning.wrap(":")}#}"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.enableInspections(JcvEmptyParameterInspection())

    // Then
    myFixture.checkHighlighting(
      true,
      true,
      true,
      false
    )
  }

  fun `test unexpected parameters on known validator expected 1 parameter but 3 provided`() {

    // Given
    val error = HighlightingMessage.error("Unexpected parameter")

    val code =
      """{#contains:param1${error.wrap(";param 2")}${error.wrap(";param 3")}#}"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.enableInspections(JcvUnexpectedParameterInspection())

    // Then
    myFixture.checkHighlighting(
      true,
      true,
      true,
      false
    )
  }

  fun `test unexpected parameter on known validator expecting no parameter but one empty provided`() {

    // Given
    val error = HighlightingMessage.error("Unexpected parameter")

    val code =
      """{#uuid${error.wrap(":")}#}"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.enableInspections(JcvUnexpectedParameterInspection())

    // Then
    myFixture.checkHighlighting(
      true,
      true,
      true,
      false
    )
  }

  fun `test required parameter on known validator expecting 1 parameter but none provided`() {

    // Given
    val error = HighlightingMessage.error("""\"the text to search for\" parameter is required""")

    val code =
      """{#${error.wrap("contains")}#}"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.enableInspections(JcvRequiredParameterInspection())

    // Then
    myFixture.checkHighlighting(
      true,
      true,
      true,
      false
    )
  }

  fun `test required parameter on known validator expecting 1 parameter but an empty one provided`() {

    // Given
    val error = HighlightingMessage.error("""\"the text to search for\" parameter is required""")

    val code =
      """{#contains${error.wrap(":")}#}"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.enableInspections(JcvRequiredParameterInspection())

    // Then
    myFixture.checkHighlighting(
      true,
      true,
      true,
      false
    )
  }

  fun `test nothing to report on correct custom validator`() {

    // Given
    val code =
      """{#my_validator:param 1;param 2#}"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.enableInspections(
      JcvWhiteSpacesInspection(),
      JcvEmptyParameterInspection(),
      JcvUnexpectedParameterInspection(),
      JcvRequiredParameterInspection(),
      JcvMissingLibraryInspection()
    )

    // Then
    myFixture.checkHighlighting(
      true,
      true,
      true,
      false
    )
  }

  fun `test missing library on JCV validator`() {

    // Given
    val warning = HighlightingMessage.warning("""Missing validator library \"com.ekino.oss.jcv:jcv-core\"""")

    val code =
      """{#${warning.wrap("uuid")}#}"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.enableInspections(
      JcvMissingLibraryInspection(),
      JcvUnknownValidatorInspection()
    )

    // Then
    myFixture.checkHighlighting(
      true,
      true,
      true,
      false
    )
  }

  fun `test missing definition on custom validator`() {

    // Given
    val warning = HighlightingMessage.warning("Unknown validator definition")

    val code =
      """{#${warning.wrap("my_validator")}:param 1;param 2#}"""

    myFixture.configureByText(JcvFileType, code)

    // When
    myFixture.enableInspections(
      JcvMissingLibraryInspection(),
      JcvUnknownValidatorInspection()
    )

    // Then
    myFixture.checkHighlighting(
      true,
      true,
      true,
      false
    )
  }
}
