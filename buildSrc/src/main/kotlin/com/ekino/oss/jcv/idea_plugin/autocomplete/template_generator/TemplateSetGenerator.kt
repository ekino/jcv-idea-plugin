package com.ekino.oss.jcv.idea_plugin.autocomplete.template_generator

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.Writer
import java.net.URL

class TemplateSetGenerator(
  val validatorsFile: URL,
  val templateWriter: Writer
) {

  fun writeTemplateSet() =
    xmlMapper().writeValue(templateWriter, loadValidatorsFromFile().toTemplateSet())

  private fun loadValidatorsFromFile(): JcvValidatorsGroup =
    yamlMapper().readValue(validatorsFile.readText(), JcvValidatorsGroup::class.java)

  private fun xmlMapper(): XmlMapper {
    val xmlMapper = XmlMapper(JacksonXmlModule())
    xmlMapper.enable(SerializationFeature.INDENT_OUTPUT)
    xmlMapper.setSerializationInclusion(Include.NON_EMPTY)

    return xmlMapper
  }

  private fun yamlMapper(): ObjectMapper {
    val yamlMapper = ObjectMapper(YAMLFactory())
    yamlMapper.registerModule(KotlinModule())

    return yamlMapper
  }
}
