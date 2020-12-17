package com.ekino.oss.jcv.idea.plugin.language.intention

import com.ekino.oss.jcv.idea.plugin.definition.JcvValidatorRegistry
import com.ekino.oss.jcv.idea.plugin.definition.existsInModule
import com.ekino.oss.jcv.idea.plugin.language.JcvBundle
import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonBooleanLiteral
import com.intellij.json.psi.JsonNumberLiteral
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.json.psi.JsonValue
import com.intellij.openapi.module.Module
import com.intellij.psi.PsiElement
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

@OptIn(ExperimentalStdlibApi::class)
fun findReplacementSuggestions(module: Module?, jsonValueElements: List<JsonValue>) = jsonValueElements
  .map { it to suggestReplacements(it) }
  .let { suggestionsByJsonValueElt ->
    suggestionsByJsonValueElt
      .map { it.second }
      .reduceOrNull { acc, list ->
        acc.intersect(list).toList()
      }
      .orEmpty()
  }
  .filter { module != null && JcvValidatorRegistry.findById(it.validatorId)?.existsInModule(module) ?: false }

private fun suggestReplacements(element: PsiElement): List<JcvValidatorSuggestion> =
  when (val value = element as? JsonValue) {
    is JsonStringLiteral -> {
      stringSuggestions(value)
    }
    is JsonNumberLiteral -> listOf(
      suggestion(SuggestionCategory.NUMBER, "number_type")
    )
    is JsonBooleanLiteral -> listOf(
      suggestion(SuggestionCategory.BOOLEAN, "boolean_type")
    )
    is JsonArray -> listOf(
      suggestion(SuggestionCategory.ARRAY, "array_type"),
      suggestion(SuggestionCategory.ARRAY, "json_array")
    )
    is JsonObject -> listOf(
      suggestion(SuggestionCategory.OBJECT, "object_type"),
      suggestion(SuggestionCategory.OBJECT, "json_object")
    )
    else -> emptyList()
  }

private fun stringSuggestions(value: JsonStringLiteral): MutableList<JcvValidatorSuggestion> {
  val rawValue = value.value
  val textValue = rawValue.escape()
  val escapedRegexValue = Regex.escape(rawValue).escape()

  val suggestions = mutableListOf<JcvValidatorSuggestion>()

  if (UUID_REGEX.matches(textValue)) {
    suggestions += suggestion(SuggestionCategory.ID, "uuid")
  }

  if (MONGO_ID_REGEX.matches(textValue)) {
    suggestions += suggestion(SuggestionCategory.ID, "mongo_id")
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
    .map { (name) -> suggestion(SuggestionCategory.DATE, "date_time_format", name) }
    .also { suggestions += it }

  if (textValue.contains("/")) {
    val category = SuggestionCategory.URL
    if (textValue.isURI()) {
      suggestions += listOf(
        suggestion(category, "url"),
        suggestion(category, "url_ending", textValue),
        suggestion(category, "url_regex", escapedRegexValue)
      )
    }

    if (textValue.contains("{") && textValue.substringBefore('{').isURI()) {
      suggestions += listOf(
        suggestion(category, "templated_url"),
        suggestion(category, "templated_url_ending", textValue),
        suggestion(category, "templated_url_regex", escapedRegexValue)
      )
    }
  }

  suggestions += SuggestionCategory.TEXT.let { category ->
    listOf(
      suggestion(category, "starts_with", textValue),
      suggestion(category, "ends_with", textValue),
      suggestion(category, "contains", textValue),
      suggestion(category, "regex", escapedRegexValue),
      suggestion(category, "not_empty"),
      suggestion(category, "not_null"),
      suggestion(category, "string_type"),
    )
  }

  return suggestions
}

private fun String.escape() = StringEscapeUtils.escapeJava(this)

private fun String.isURI() = try {
  URI(this)
  true
} catch (e: URISyntaxException) {
  false
}

private fun suggestion(category: SuggestionCategory? = null, validatorId: String, vararg parameters: String) =
  JcvValidatorSuggestion(
    category = category,
    validatorId = validatorId,
    parameters = parameters.toList()
  )

enum class SuggestionCategory(val title: String) {
  ID(JcvBundle.getMessage("jcv.replacement.suggestions.categories.id.title")),
  URL(JcvBundle.getMessage("jcv.replacement.suggestions.categories.url.title")),
  DATE(JcvBundle.getMessage("jcv.replacement.suggestions.categories.date.title")),
  TEXT(JcvBundle.getMessage("jcv.replacement.suggestions.categories.text.title")),
  NUMBER(JcvBundle.getMessage("jcv.replacement.suggestions.categories.number.title")),
  BOOLEAN(JcvBundle.getMessage("jcv.replacement.suggestions.categories.boolean.title")),
  ARRAY(JcvBundle.getMessage("jcv.replacement.suggestions.categories.array.title")),
  OBJECT(JcvBundle.getMessage("jcv.replacement.suggestions.categories.object.title"))
}

data class JcvValidatorSuggestion(
  val category: SuggestionCategory? = null,
  val validatorId: String,
  val parameters: List<String> = emptyList()
) {
  fun asText(): String {
    val parametersAsText = parameters.takeIf { it.isNotEmpty() }
      ?.joinToString(prefix = ":", separator = ";")
      .orEmpty()
    return "{#$validatorId$parametersAsText#}"
  }
}
