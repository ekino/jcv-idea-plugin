package com.ekino.oss.jcv.idea.plugin.definition.custom

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.io.exists
import java.io.IOException
import java.nio.file.Path

private val objectMapper by lazy {
  jacksonObjectMapper().also {
    it.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    it.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  }
}

const val DEFINITIONS_FILE_NAME = ".jcvdefinitions.json"

object ValidatorDescriptionsResolver {

  fun definitionsFileName() = DEFINITIONS_FILE_NAME

  fun definitionsFilePath(project: Project): Path? = project.basePath
    ?.let { Path.of(it).resolve(DEFINITIONS_FILE_NAME) }

  fun getDefinitionsSource(project: Project) = definitionsFilePath(project)
    ?.let { VfsUtil.findFile(it, true) }

  fun getOrCreateDefinitionsSource(project: Project) = definitionsFilePath(project)
    ?.also { filePath ->
      if (!filePath.exists()) {
        filePath.toFile().createNewFile()
      }
    }
    ?.let { getDefinitionsSource(project) }

  fun write(definitions: JcvValidatorDefinitions, virtualFile: VirtualFile) {
    val content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(definitions)
    VfsUtil.saveText(virtualFile, content)
  }

  fun parse(virtualFile: VirtualFile) = virtualFile
    .let {
      try {
        objectMapper.readValue<JcvValidatorDefinitions>(it.inputStream)
      } catch (e: IOException) {
        null
      }
    }
}
