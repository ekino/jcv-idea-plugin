package com.ekino.oss.jcv.idea.plugin.language.syntax

import com.ekino.oss.jcv.idea.plugin.language.JcvLexerAdapter
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class JcvSyntaxHighlighter : SyntaxHighlighterBase() {

  companion object {
    val VALIDATOR_MARKERS = createTextAttributesKey(
      "JCV.VALIDATOR_MARKERS", DefaultLanguageHighlighterColors.KEYWORD
    )

    val VALIDATOR_ID = createTextAttributesKey(
      "JCV.VALIDATOR_ID", DefaultLanguageHighlighterColors.IDENTIFIER
    )

    val VALIDATOR_PARAMETER = createTextAttributesKey(
      "JCV.VALIDATOR_PARAMETER", DefaultLanguageHighlighterColors.STRING
    )

    val BAD_CHARACTER = createTextAttributesKey(
      "JCV.BAD_CHARACTER", HighlighterColors.BAD_CHARACTER
    )

    private val tokenToTextAttributesKey = mapOf(
      JcvTypes.VALIDATOR_START_MARKER to VALIDATOR_MARKERS,
      JcvTypes.VALIDATOR_END_MARKER to VALIDATOR_MARKERS,
      JcvTypes.VALIDATOR_ID to VALIDATOR_ID,
      JcvTypes.PARAMETERS_START_MARKER to VALIDATOR_MARKERS,
      JcvTypes.PARAMETER_SEPARATOR to VALIDATOR_MARKERS,
      JcvTypes.PARAMETER to VALIDATOR_PARAMETER,
      TokenType.BAD_CHARACTER to BAD_CHARACTER
    )
  }

  override fun getHighlightingLexer(): Lexer = JcvLexerAdapter()

  override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
    pack(tokenToTextAttributesKey[tokenType])
}
