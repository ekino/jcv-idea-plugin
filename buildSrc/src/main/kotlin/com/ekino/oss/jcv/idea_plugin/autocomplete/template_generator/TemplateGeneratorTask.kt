package com.ekino.oss.jcv.idea_plugin.autocomplete.template_generator

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class TemplateGeneratorTask : DefaultTask() {

  lateinit var resourceFiles: Map<String, File>

  @TaskAction
  fun generateTemplateFiles() {
    resourceFiles
      .map { resourceFile ->
        getValidatorsFile(resourceFile.key)?.let {
          TemplateSetGenerator(it, resourceFile.value.writer())
        }
      }
      .filterNotNull()
      .forEach { it.writeTemplateSet() }
  }

  private fun getValidatorsFile(validatorsGroup: String) =
    this::class.java.classLoader.getResource("$validatorsGroup-validators.yml")
}
