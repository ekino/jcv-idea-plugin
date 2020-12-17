package com.ekino.oss.jcv.idea.plugin.language.inspection

import com.ekino.oss.jcv.idea.plugin.language.JcvBasePlatformTestCase
import com.ekino.oss.jcv.idea.plugin.language.JcvFileType
import com.intellij.openapi.module.Module

class JcvInspectionsWithLibsTest : JcvBasePlatformTestCase() {

  override fun configureModule(module: Module) {
    module.addAllJcvLibraries()
  }

  fun `test nothing to report on correct known validator`() {

    // Given
    val code = """{#date_time_format:iso_instant;fr-FR#}"""

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
      true, true, true, false
    )
  }
}
