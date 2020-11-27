package com.ekino.oss.jcv.idea_plugin.definition

import com.ekino.oss.jcv.idea_plugin.service.JcvDefinitionsCache
import com.ekino.oss.jcv.idea_plugin.service.JcvLibraryCache
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.roots.libraries.Library
import com.intellij.psi.PsiElement

fun PsiElement.findModule() = ModuleUtil.findModuleForPsiElement(this)

fun JcvValidatorDefinition.existsInModule(module: Module): Boolean = when (origin) {
  is LibraryOrigin -> (origin as LibraryOrigin).findLibrary(module) != null
  is ProjectOrigin -> JcvDefinitionsCache.getValidators(module).any { it.id == this.id }
  else -> false
}

fun LibraryOrigin.findLibrary(module: Module): Library? = dependencyPattern()
  ?.let { dependencyPattern ->
    JcvLibraryCache.getLibraries(module).find { it.name?.contains(dependencyPattern) ?: false }
  }

