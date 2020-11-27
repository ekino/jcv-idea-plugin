package com.ekino.oss.jcv.idea_plugin.language

const val TEST_DATA_DIR = "src/test/testData"

data class HighlightingMessage(
  val level: String,
  val description: String
) {
  companion object {
    fun error(description: String) = HighlightingMessage("error", description)
    fun warning(description: String) = HighlightingMessage("warning", description)
  }

  private fun tagStart() = """<$level descr="$description">"""
  private fun tagEnd() = """</$level>"""
  fun wrap(wrappedText: String) = """${tagStart()}$wrappedText${tagEnd()}"""
}