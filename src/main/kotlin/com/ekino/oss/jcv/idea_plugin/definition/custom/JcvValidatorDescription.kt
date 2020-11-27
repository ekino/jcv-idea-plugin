package com.ekino.oss.jcv.idea_plugin.definition.custom

import com.ekino.oss.jcv.idea_plugin.definition.impl.JcvProjectValidatorDefinition
import com.ekino.oss.jcv.idea_plugin.definition.impl.ParameterDefinitionUtils
import com.intellij.openapi.vfs.VirtualFile

data class JcvValidatorDefinitions(
  val validators: List<JcvValidatorDescription> = emptyList()
)

data class JcvValidatorDescription(
  val id: String,
  val parameters: List<JcvValidatorParameterDescription> = emptyList()
)

data class JcvValidatorParameterDescription(
  val description: String? = null,
  val required: Boolean = false,
  val suggestedValues: List<String> = emptyList()
)

fun JcvValidatorDescription.toDefinition(virtualFile: VirtualFile) = JcvProjectValidatorDefinition(
  id = id,
  parameters = parameters.map {
    ParameterDefinitionUtils.parameterDefinition(
      description = it.description,
      required = it.required,
      suggestedValues = it.suggestedValues
    )
  },
  virtualFile = virtualFile
)
