package com.ekino.oss.jcv.idea_plugin.language

import com.ekino.oss.jcv.idea_plugin.definition.custom.ValidatorDescriptionsResolver
import com.ekino.oss.jcv.idea_plugin.service.JcvDefinitionsCache
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

const val LAST_JCV_CORE_VERSION = "1.5.0"

const val LAST_JCV_DB_VERSION = "0.0.4"

abstract class JcvBasePlatformTestCase : BasePlatformTestCase() {

  override fun getProjectDescriptor(): LightProjectDescriptor = object : LightProjectDescriptor() {
    override fun setUpProject(project: Project, handler: SetupHandler) {
      addCustomDefinitionsToProjectBasePath(project)
      super.setUpProject(project, handler)
    }

    override fun createModule(project: Project, moduleFilePath: String): Module {
      return super.createModule(project, moduleFilePath)
        .also { configureModule(it) }
    }
  }

  /**
   * @see: <a href="https://intellij-support.jetbrains.com/hc/en-us/community/posts/115000539544/comments/115000469650">Plugin testing accesses to Library and Java classes</a<
   */
  fun Module.addEmptyLibrary(libraryName: String) = PsiTestUtil.newLibrary(libraryName).addTo(this)

  fun Module.addJcvCore(version: String = LAST_JCV_CORE_VERSION) =
    addEmptyLibrary("com.ekino.oss.jcv:jcv-core:$version")

  fun Module.addJcvDbCore(version: String = LAST_JCV_DB_VERSION) =
    addEmptyLibrary("com.ekino.oss.jcv-db:jcv-db-core:$version")

  fun Module.addJcvDbMongo(version: String = LAST_JCV_DB_VERSION) =
    addEmptyLibrary("com.ekino.oss.jcv-db:jcv-db-mongo:$version")

  fun Module.addAllJcvLibraries() {
    addJcvCore()
    addJcvDbCore()
    addJcvDbMongo()
  }

  protected open fun configureModule(module: Module) {}

  protected open fun getCustomDefinitions(): String? = null

  fun addCustomDefinitionsToProjectBasePath(targetProject: Project) {
    val definitionsContent = getCustomDefinitions() ?: return
    definitionsFile(targetProject)
      ?.also {
        it.writeText(definitionsContent)
      }
  }

  override fun tearDown() {
    definitionsFile(project)?.delete()
    JcvDefinitionsCache.clear()
    super.tearDown()
  }

  private fun definitionsFile(targetProject: Project) =
    ValidatorDescriptionsResolver.definitionsFilePath(targetProject)
      ?.toFile()
}