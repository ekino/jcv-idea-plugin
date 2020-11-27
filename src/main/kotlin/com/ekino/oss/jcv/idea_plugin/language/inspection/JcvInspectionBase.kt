package com.ekino.oss.jcv.idea_plugin.language.inspection

import com.ekino.oss.jcv.idea_plugin.language.JcvLanguage
import com.intellij.codeInspection.LocalInspectionTool

abstract class JcvInspectionBase : LocalInspectionTool() {
  override fun getDisplayName(): String = JcvLanguage.id
}
