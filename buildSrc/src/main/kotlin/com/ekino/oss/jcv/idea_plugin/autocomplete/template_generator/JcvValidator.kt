package com.ekino.oss.jcv.idea_plugin.autocomplete.template_generator

data class JcvValidatorsGroup(
  val group: String,
  val validators: List<JcvValidator>
)

data class JcvValidator(
  val name: String,
  val value: String? = null,
  val description: String,
  val variables: Map<String, String>? = emptyMap()
)

fun JcvValidatorsGroup.toTemplateSet(): TemplateSet =
  TemplateSet(
    group = this.group,
    templates = validators.sortedBy { it.name }.map { it.toTemplate() }
  )

fun JcvValidator.toTemplate(): Template =
  Template(
    name = this.name,
    value = this.value ?: this.name,
    description = this.description,
    variables = this.variables?.map { Variable(name = it.key, expression = it.value) }
  )
