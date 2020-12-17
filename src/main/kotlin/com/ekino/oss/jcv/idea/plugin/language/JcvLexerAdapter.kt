package com.ekino.oss.jcv.idea.plugin.language

import com.intellij.lexer.FlexAdapter

class JcvLexerAdapter : FlexAdapter(JcvLexer(null))
