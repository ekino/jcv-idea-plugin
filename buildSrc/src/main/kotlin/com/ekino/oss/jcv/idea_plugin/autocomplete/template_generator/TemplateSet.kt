package com.ekino.oss.jcv.idea_plugin.autocomplete.template_generator

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

private const val JSON_OPTION = "JSON"
private const val JSON_PROPERTY_OPTION = "JSON_PROPERTY_KEYS"

@JacksonXmlRootElement(localName = "templateSet")
data class TemplateSet(
  @get:JacksonXmlProperty(isAttribute = true)
  val group: String,

  @get:JacksonXmlProperty(localName = "template")
  @JacksonXmlElementWrapper(useWrapping = false)
  val templates: List<Template>
)

data class Template(
  @get:JacksonXmlProperty(isAttribute = true)
  val name: String,
  @get:JacksonXmlProperty(isAttribute = true)
  val value: String,
  @get:JacksonXmlProperty(isAttribute = true)
  val description: String,
  @get:JacksonXmlProperty(isAttribute = true)
  val toReformat: Boolean? = false,
  @get:JacksonXmlProperty(isAttribute = true)
  val toShortenFQNames: Boolean? = true,

  @get:JacksonXmlProperty(localName = "variable")
  @JacksonXmlElementWrapper(useWrapping = false)
  val variables: List<Variable>?,

  val context: Context? = Context(options = listOf(
    Option(name = JSON_OPTION, value = true.toString()),
    Option(name = JSON_PROPERTY_OPTION, value = false.toString())
  ))
)

data class Variable(
  @get:JacksonXmlProperty(isAttribute = true)
  val name: String,
  @get:JacksonXmlProperty(isAttribute = true)
  val expression: String? = "",
  @get:JacksonXmlProperty(isAttribute = true)
  val defaultValue: String? = "",
  @get:JacksonXmlProperty(isAttribute = true)
  val alwaysStopAt: Boolean? = true
)

data class Context(
  @get:JacksonXmlProperty(localName = "option")
  @JacksonXmlElementWrapper(useWrapping = false)
  val options: List<Option>
)

data class Option(
  @get:JacksonXmlProperty(isAttribute = true)
  val name: String,
  @get:JacksonXmlProperty(isAttribute = true)
  val value: String
)
