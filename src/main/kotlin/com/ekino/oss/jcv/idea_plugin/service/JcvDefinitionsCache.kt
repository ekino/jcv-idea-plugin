package com.ekino.oss.jcv.idea_plugin.service

import com.ekino.oss.jcv.idea_plugin.definition.custom.ValidatorDescriptionsResolver
import com.ekino.oss.jcv.idea_plugin.definition.custom.toDefinition
import com.ekino.oss.jcv.idea_plugin.definition.impl.JcvProjectValidatorDefinition
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project

private val validatorsDefinitionsByProject = mutableMapOf<Project, List<JcvProjectValidatorDefinition>>()

object JcvDefinitionsCache {

  fun getAllValidators() = validatorsDefinitionsByProject.values.flatten()

  fun getValidators(module: Module) = validatorsDefinitionsByProject[module.project] ?: emptyList()

  fun refreshCache(project: Project) {
    ValidatorDescriptionsResolver.getDefinitionsSource(project)
      ?.let { file ->
        ValidatorDescriptionsResolver.parse(file)
          ?.validators
          ?.map { it.toDefinition(file) }
          ?: emptyList()
      }
      .let { it ?: emptyList() }
      .also { validatorsDefinitionsByProject[project] = it }
  }

  fun refreshCache(module: Module) {
    refreshCache(module.project)
  }

  fun clear() {
    validatorsDefinitionsByProject.clear()
  }
}