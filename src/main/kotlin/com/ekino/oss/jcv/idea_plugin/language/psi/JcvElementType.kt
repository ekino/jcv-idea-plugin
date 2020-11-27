package com.ekino.oss.jcv.idea_plugin.language.psi

import com.ekino.oss.jcv.idea_plugin.language.JcvLanguage
import com.intellij.psi.tree.IElementType

class JcvElementType(debugName: String) : IElementType(debugName, JcvLanguage) {

  private val printableName = debugName.replace("_", " ")
    .toLowerCase()
    .capitalize()

  override fun toString(): String = printableName
}
