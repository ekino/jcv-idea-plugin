package com.ekino.oss.jcv.idea_plugin.language.psi

import com.ekino.oss.jcv.idea_plugin.language.JcvFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil

object JcvElementFactory {

  @JvmStatic
  fun createValidator(project: Project, name: String): JcvValidator? =
    PsiTreeUtil.findChildOfType(createFile(project, """{#$name#}"""), JcvValidator::class.java)

  @JvmStatic
  fun createFile(project: Project, text: String): JcvFile = PsiFileFactory.getInstance(project)
    .createFileFromText("dummy.jcv", JcvFileType, text) as JcvFile
}
