package com.ekino.oss.jcv.idea.plugin.definition.impl

import com.ekino.oss.jcv.idea.plugin.definition.JcvValidatorDefinition
import com.ekino.oss.jcv.idea.plugin.definition.JcvValidatorOrigin
import com.ekino.oss.jcv.idea.plugin.definition.ParameterDefinition
import com.ekino.oss.jcv.idea.plugin.definition.ProjectOrigin
import com.intellij.openapi.vfs.VirtualFile

class JcvProjectValidatorDefinition(
  override val id: String,
  override val parameters: List<ParameterDefinition> = emptyList(),
  virtualFile: VirtualFile
) : JcvValidatorDefinition {

  override val origin: JcvValidatorOrigin =
    ProjectOrigin(virtualFile)
}
