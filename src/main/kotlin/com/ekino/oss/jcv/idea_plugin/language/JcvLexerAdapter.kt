package com.ekino.oss.jcv.idea_plugin.language

import com.intellij.lexer.FlexAdapter

class JcvLexerAdapter : FlexAdapter(JcvLexer(null))
