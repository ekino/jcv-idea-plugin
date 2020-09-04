package com.ekino.oss.jcv.idea_plugin.suggestion

import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonBooleanLiteral
import com.intellij.json.psi.JsonNumberLiteral
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.json.psi.JsonValue
import org.apache.commons.text.StringEscapeUtils
import java.net.URI
import java.net.URISyntaxException
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val UUID_REGEX = """^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$""".toRegex()
private val MONGO_ID_REGEX = """^[a-f\d]{24}$""".toRegex()
private val DATE_FORMATTERS by lazy {
  listOf(
    "basic_iso_date" to DateTimeFormatter.BASIC_ISO_DATE,
    "iso_local_date" to DateTimeFormatter.ISO_LOCAL_DATE,
    "iso_offset_date" to DateTimeFormatter.ISO_OFFSET_DATE,
    "iso_date" to DateTimeFormatter.ISO_DATE,
    "iso_local_time" to DateTimeFormatter.ISO_LOCAL_TIME,
    "iso_offset_time" to DateTimeFormatter.ISO_OFFSET_TIME,
    "iso_time" to DateTimeFormatter.ISO_TIME,
    "iso_local_date_time" to DateTimeFormatter.ISO_LOCAL_DATE_TIME,
    "iso_offset_date_time" to DateTimeFormatter.ISO_OFFSET_DATE_TIME,
    "iso_zoned_date_time" to DateTimeFormatter.ISO_ZONED_DATE_TIME,
    "iso_date_time" to DateTimeFormatter.ISO_DATE_TIME,
    "iso_ordinal_date" to DateTimeFormatter.ISO_ORDINAL_DATE,
    "iso_week_date" to DateTimeFormatter.ISO_WEEK_DATE,
    "iso_instant" to DateTimeFormatter.ISO_INSTANT,
    "rfc_1123_date_time" to DateTimeFormatter.RFC_1123_DATE_TIME
  )
}

class ValueToDefaultValidatorsSuggestion : ValueToValidatorSuggestion {
  override fun suggest(value: JsonValue): List<String> {

    return when (value) {
      is JsonStringLiteral -> {
        val rawValue = value.value
        val textValue = rawValue.escape()
        val escapedRegexValue = Regex.escape(rawValue).escape()
        val suggestions = mutableListOf(
          "starts_with:$textValue",
          "ends_with:$textValue",
          "contains:$textValue",
          "regex:$escapedRegexValue",
          "not_empty",
          "not_null",
          "string_type"
        )

        if (UUID_REGEX.matches(textValue)) {
          suggestions += "uuid"
        }

        if (MONGO_ID_REGEX.matches(textValue)) {
          suggestions += "mongo_id"
        }

        if (textValue.contains("/")) {
          if (textValue.isURI()) {
            suggestions += listOf(
              "url",
              "url_ending:$textValue",
              "url_regex:$escapedRegexValue"
            )
          }

          if (textValue.contains("{") && textValue.substringBefore('{').isURI()) {
            suggestions += listOf(
              "templated_url",
              "templated_url_ending:$textValue",
              "templated_url_regex:$escapedRegexValue"
            )
          }
        }

        DATE_FORMATTERS
          .filter { (_, formatter) ->
            try {
              formatter.parse(textValue)
              true
            } catch (e: DateTimeParseException) {
              false
            }
          }
          .map { (name) -> "date_time_format:$name" }
          .also { suggestions += it }

        suggestions
      }
      is JsonBooleanLiteral -> listOf("boolean_type")
      is JsonNumberLiteral -> listOf("number_type")
      is JsonArray -> listOf("array_type", "json_array")
      is JsonObject -> listOf("object_type", "json_object")
      else -> emptyList()
    }
      .map { "\"{#$it#}\"" }
  }
}

private fun String.escape() = StringEscapeUtils.escapeJava(this)

private fun String.isURI() = try {
  URI(this)
  true
} catch (e: URISyntaxException) {
  false
}
