package com.ekino.oss.jcv.idea.plugin.definition

import com.ekino.oss.jcv.idea.plugin.service.JcvDefinitionsCache
import com.ekino.oss.jcv.idea.plugin.service.JcvLibraryCache
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.roots.libraries.Library
import com.intellij.psi.PsiElement

fun PsiElement.findModule(): Module? {
  ModuleUtil.findModuleForPsiElement(this)?.also { return it }

  // search by original file for fragment edition
  // https://youtrack.jetbrains.com/issue/IJSDK-975
  return this.containingFile?.originalFile?.let { ModuleUtil.findModuleForPsiElement(it) }
}

fun JcvValidatorDefinition.existsInModule(module: Module): Boolean = when (origin) {
  is LibraryOrigin -> (origin as LibraryOrigin).findLibrary(module) != null
  is ProjectOrigin -> JcvDefinitionsCache.getValidators(module).any { it.id == this.id }
  else -> false
}

fun LibraryOrigin.findLibrary(module: Module): Library? = dependencyPattern()
  ?.let { dependencyPattern ->
    JcvLibraryCache.getLibraries(module).find { it.name?.contains(dependencyPattern) ?: false }
  }
