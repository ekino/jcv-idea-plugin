package com.ekino.oss.jcv.idea_plugin.service

import com.ekino.oss.jcv.idea_plugin.definition.JcvValidatorRegistry
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.libraries.Library

private val validatorLibrariesByModule = mutableMapOf<Module, List<Library>>()

object JcvLibraryCache {

  fun getLibraries(module: Module) = validatorLibrariesByModule[module] ?: emptyList()

  fun refreshCache(project: Project) {
    ModuleManager.getInstance(project)
      .modules
      .forEach { refreshCache(it) }
  }

  fun refreshCache(module: Module) {
    val dependencyPatterns = JcvValidatorRegistry.getLibraryOrigins()
      .mapNotNull { it.dependencyPattern() }
      .toSet()

    module.filterLibraries { library ->
      dependencyPatterns.any { library.name?.contains(it) ?: false }
    }
      .also { validatorLibrariesByModule[module] = it }
  }
}

private fun Module.filterLibraries(filter: (Library) -> Boolean): List<Library> {
  val module = this
  val results = mutableListOf<Library>()
  ModuleRootManager.getInstance(module)
    .orderEntries()
    .forEachLibrary { library ->
      library.takeIf(filter)
        ?.also { results.add(it) }
      true // continue
    }
  return results
}
