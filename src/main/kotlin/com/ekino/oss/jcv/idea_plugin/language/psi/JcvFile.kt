package com.ekino.oss.jcv.idea_plugin.language.psi

import com.ekino.oss.jcv.idea_plugin.language.JcvFileType
import com.ekino.oss.jcv.idea_plugin.language.JcvLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class JcvFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, JcvLanguage) {

  override fun getFileType(): FileType = JcvFileType

  override fun toString(): String = "Jcv File"
}
