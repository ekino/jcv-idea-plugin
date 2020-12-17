package com.ekino.oss.jcv.idea.plugin.language

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object JcvFileType : LanguageFileType(JcvLanguage) {

  override fun getName(): String = "JCV"

  override fun getDescription(): String = "JCV language file"

  override fun getDefaultExtension(): String = "jcv"

  override fun getIcon(): Icon = JcvIcons.FILE
}
