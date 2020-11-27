package com.ekino.oss.jcv.idea_plugin.definition.impl

import com.ekino.oss.jcv.idea_plugin.definition.DocumentationUriProvider
import com.ekino.oss.jcv.idea_plugin.definition.JcvValidatorDefinition
import com.ekino.oss.jcv.idea_plugin.definition.LibraryOrigin
import com.ekino.oss.jcv.idea_plugin.definition.ParameterDefinition
import com.ekino.oss.jcv.idea_plugin.definition.impl.ParameterDefinitionUtils.parameterDefinition
import java.net.URI
import java.util.Locale

private val JCV_CORE_BASE_URI = URI("https://ekino.github.io/jcv/")
private val JCV_CORE_GLOBAL_DOCUMENTATION = JCV_CORE_BASE_URI.resolve("documentation.html")
private val JCV_CORE_LIBRARY_ORIGIN = LibraryOrigin(
  displayName = "JCV Core",
  artifactId = "jcv-core",
  groupId = "com.ekino.oss.jcv",
  documentation = JCV_CORE_GLOBAL_DOCUMENTATION
)
private val JCV_CORE_VALIDATORS_DOCUMENTATION = JCV_CORE_BASE_URI.resolve("documentation/validators.html")

internal class JcvCoreValidatorDefinition(
  override val id: String,
  override val parameters: List<ParameterDefinition> = emptyList()
) : JcvValidatorDefinition, DocumentationUriProvider {

  override val origin = JCV_CORE_LIBRARY_ORIGIN

  override val documentation: URI = JCV_CORE_VALIDATORS_DOCUMENTATION.resolve("#$id")
}

val JCV_CORE_VALIDATORS: List<JcvValidatorDefinition> by lazy {
  listOf(
    JcvCoreValidatorDefinition(
      id = "contains",
      parameters = listOf(
        parameterDefinition(
          description = "the text to search for",
          required = true
        )
      )
    ),
    JcvCoreValidatorDefinition(
      id = "starts_with",
      parameters = listOf(
        parameterDefinition(
          description = "the text to search for",
          required = true
        )
      )
    ),
    JcvCoreValidatorDefinition(
      id = "ends_with",
      parameters = listOf(
        parameterDefinition(
          description = "the text to search for",
          required = true
        )
      )
    ),
    JcvCoreValidatorDefinition(
      id = "regex",
      parameters = listOf(
        parameterDefinition(
          description = "the regex pattern",
          required = true
        )
      )
    ),
    JcvCoreValidatorDefinition(
      id = "uuid"
    ),
    JcvCoreValidatorDefinition(
      id = "not_null"
    ),
    JcvCoreValidatorDefinition(
      id = "not_empty"
    ),
    JcvCoreValidatorDefinition(
      id = "boolean_type"
    ),
    JcvCoreValidatorDefinition(
      id = "string_type"
    ),
    JcvCoreValidatorDefinition(
      id = "number_type"
    ),
    JcvCoreValidatorDefinition(
      id = "array_type"
    ),
    JcvCoreValidatorDefinition(
      id = "object_type"
    ),
    JcvCoreValidatorDefinition(
      id = "url"
    ),
    JcvCoreValidatorDefinition(
      id = "url_ending",
      parameters = listOf(
        parameterDefinition(
          description = "url ending",
          required = true
        )
      )
    ),
    JcvCoreValidatorDefinition(
      id = "url_regex",
      parameters = listOf(
        parameterDefinition(
          description = "regex pattern",
          required = true
        )
      )
    ),
    JcvCoreValidatorDefinition(
      id = "templated_url"
    ),
    JcvCoreValidatorDefinition(
      id = "templated_url_ending",
      parameters = listOf(
        parameterDefinition(
          description = "templated url ending",
          required = true
        )
      )
    ),
    JcvCoreValidatorDefinition(
      id = "templated_url_regex",
      parameters = listOf(
        parameterDefinition(
          description = "regex pattern",
          required = true
        )
      )
    ),
    JcvCoreValidatorDefinition(
      id = "date_time_format",
      parameters = listOf(
        parameterDefinition(
          description = "the date time pattern",
          required = true,
          suggestedValues = listOf(
            "iso_instant",
            "iso_local_date",
            "iso_offset_date",
            "iso_date",
            "iso_local_time",
            "iso_offset_time",
            "iso_time",
            "iso_local_date_time",
            "iso_offset_date_time",
            "iso_zoned_date_time",
            "iso_date_time",
            "iso_ordinal_date",
            "iso_week_date",
            "basic_iso_date",
            "rfc_1123_date_time"
          )
        ),
        parameterDefinition(
          description = "the language tag",
          required = false,
          suggestedValues = Locale.getAvailableLocales()
            .map { it.toLanguageTag() }
            .sorted()
        )
      )
    )
  )
}