package com.ekino.oss.jcv.idea_plugin.language.parsing

import com.ekino.oss.jcv.idea_plugin.language.JcvFileType
import com.ekino.oss.jcv.idea_plugin.language.JcvParserDefinition
import com.ekino.oss.jcv.idea_plugin.language.TEST_DATA_DIR
import com.intellij.testFramework.ParsingTestCase

/**
 * @see <a href="https://jetbrains.org/intellij/sdk/docs/tutorials/writing_tests_for_plugins/parsing_test.html#define-a-parsing-test">2.4. Define a Parsing Test</a>
 */
class JcvParsingTest : ParsingTestCase(
  "",
  JcvFileType.defaultExtension,
  JcvParserDefinition()
) {

  fun testParsingValidatorWithIdOnly() {
    doTest(true)
  }

  fun testParsingValidatorWithEmptyParameter() {
    doTest(true)
  }

  fun testParsingValidatorWithEmptyParameters() {
    doTest(true)
  }

  fun testParsingValidatorWithEscapedParameterSeparator() {
    doTest(true)
  }

  fun testParsingValidatorWithOneParameter() {
    doTest(true)
  }

  fun testParsingValidatorWithTwoParameters() {
    doTest(true)
  }

  fun testParsingValidatorWithWhiteSpaces() {
    doTest(true)
  }

  fun testParsingValidatorWithTwoEndMarkers() {
    doTest(true)
  }

  override fun getTestDataPath(): String = "$TEST_DATA_DIR/parsing"

  override fun skipSpaces(): Boolean = false

  override fun includeRanges(): Boolean = true
}