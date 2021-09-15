package com.ekino.oss.jcv.idea.plugin.language.psi

import com.ekino.oss.jcv.idea.plugin.language.JcvLanguage
import com.intellij.psi.tree.IElementType
import java.util.*

class JcvTokenType(debugName: String) : IElementType(debugName, JcvLanguage) {

  private val printableName = debugName.replace("_", " ")
    .lowercase()
    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

  override fun toString(): String = printableName
}
