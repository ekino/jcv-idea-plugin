package com.ekino.oss.jcv.idea_plugin.definition

import java.util.Locale

enum class JcvOrigin(val displayName: String) {
  CORE("JCV Core"),
  DB_CORE("JCV DB Core"),
  DB_MONGO("JCV DB Mongo")
}

data class TemplatedValidator(
  val id: String,
  val origin: JcvOrigin = JcvOrigin.CORE,
  val parameters: List<TemplatedValidatorParameter> = emptyList()
)

data class TemplatedValidatorParameter(
  val description: String,
  val required: Boolean,
  val possibleValues: List<String> = emptyList()
)

val validators = listOf(
  // JCV Core
  TemplatedValidator(
    id = "contains",
    parameters = listOf(
      TemplatedValidatorParameter(
        description = "the text to search for",
        required = true
      )
    )
  ),
  TemplatedValidator(
    id = "starts_with",
    parameters = listOf(
      TemplatedValidatorParameter(
        description = "the text to search for",
        required = true
      )
    )
  ),
  TemplatedValidator(
    id = "ends_with",
    parameters = listOf(
      TemplatedValidatorParameter(
        description = "the text to search for",
        required = true
      )
    )
  ),
  TemplatedValidator(
    id = "regex",
    parameters = listOf(
      TemplatedValidatorParameter(
        description = "the regex pattern",
        required = true
      )
    )
  ),
  TemplatedValidator(
    id = "uuid"
  ),
  TemplatedValidator(
    id = "not_null"
  ),
  TemplatedValidator(
    id = "not_empty"
  ),
  TemplatedValidator(
    id = "boolean_type"
  ),
  TemplatedValidator(
    id = "string_type"
  ),
  TemplatedValidator(
    id = "array_type"
  ),
  TemplatedValidator(
    id = "object_type"
  ),
  TemplatedValidator(
    id = "url"
  ),
  TemplatedValidator(
    id = "url_ending",
    parameters = listOf(
      TemplatedValidatorParameter(
        description = "url ending",
        required = true
      )
    )
  ),
  TemplatedValidator(
    id = "url_regex",
    parameters = listOf(
      TemplatedValidatorParameter(
        description = "regex pattern",
        required = true
      )
    )
  ),
  TemplatedValidator(
    id = "templated_url"
  ),
  TemplatedValidator(
    id = "templated_url_ending",
    parameters = listOf(
      TemplatedValidatorParameter(
        description = "templated url ending",
        required = true
      )
    )
  ),
  TemplatedValidator(
    id = "templated_url_regex",
    parameters = listOf(
      TemplatedValidatorParameter(
        description = "regex pattern",
        required = true
      )
    )
  ),
  TemplatedValidator(
    id = "date_time_format",
    parameters = listOf(
      TemplatedValidatorParameter(
        description = "the date time pattern",
        required = true,
        possibleValues = listOf(
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
      TemplatedValidatorParameter(
        description = "the language tag",
        required = false,
        possibleValues = Locale.getAvailableLocales()
          .map { it.toLanguageTag() }
          .sorted()
      )
    )
  ),

  // JCV DB Core
  TemplatedValidator(
    id = "json_object",
    origin = JcvOrigin.DB_CORE
  ),
  TemplatedValidator(
    id = "json_array",
    origin = JcvOrigin.DB_CORE
  ),

  // JCV DB Mongo
  TemplatedValidator(
    id = "mongo_id",
    origin = JcvOrigin.DB_MONGO
  )
)

fun validatorForId(id: String) = validators.find { it.id == id }
