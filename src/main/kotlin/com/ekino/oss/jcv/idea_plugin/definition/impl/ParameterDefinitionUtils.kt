package com.ekino.oss.jcv.idea_plugin.definition.impl

import com.ekino.oss.jcv.idea_plugin.definition.ParameterDefinition

object ParameterDefinitionUtils {

  fun parameterDefinition(
    description: String?,
    required: Boolean = false,
    suggestedValues: List<String> = emptyList()
  ): ParameterDefinition = object : ParameterDefinition {
    override val description: String? = description

    override val required: Boolean = required

    override val suggestedValues: List<String> = suggestedValues
  }
}