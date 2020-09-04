package com.ekino.oss.jcv.idea_plugin.parsing

import com.ekino.oss.jcv.idea_plugin.utils.toTextRange
import com.intellij.openapi.util.TextRange

private const val WHITE_SPACES_BEFORE_GROUP_NAME = "whiteSpacesBefore"
private const val VALIDATOR_GROUP_NAME = "validator"
private const val WHITE_SPACES_AFTER_GROUP_NAME = "whiteSpacesAfter"
private const val VALIDATOR_START_MARKER_GROUP_NAME = "validatorStartMarker"
private const val VALIDATOR_CONTENT_GROUP_NAME = "validatorContent"
private const val VALIDATOR_ID_GROUP_NAME = "id"
private const val VALIDATOR_PARAMETERS_MARKER_GROUP_NAME = "parametersStartMarker"
private const val VALIDATOR_PARAMETERS_GROUP_NAME = "parameters"
private const val VALIDATOR_END_MARKER_GROUP_NAME = "validatorEndMarker"

private const val VALIDATOR_PARAMETERS_START_MARKER = ':'
private const val VALIDATOR_PARAMETERS_SEPARATOR = ';'

private val CONTENT_WITH_SPACES_REGEX =
  """^(?<$WHITE_SPACES_BEFORE_GROUP_NAME>\s*)(?<$VALIDATOR_GROUP_NAME>(?<$VALIDATOR_START_MARKER_GROUP_NAME>\{#)(?<$VALIDATOR_CONTENT_GROUP_NAME>.+?)(?<$VALIDATOR_END_MARKER_GROUP_NAME>#}))(?<$WHITE_SPACES_AFTER_GROUP_NAME>\s*)$""".toRegex()

private val VALIDATOR_CONTENT_TEMPLATE_REGEX =
  """^(?<$VALIDATOR_ID_GROUP_NAME>[\w-_.]+)((?<$VALIDATOR_PARAMETERS_MARKER_GROUP_NAME>$VALIDATOR_PARAMETERS_START_MARKER)(?<$VALIDATOR_PARAMETERS_GROUP_NAME>.*))?$""".toRegex()

private val VALIDATOR_PARAMETERS_REGEX =
  """(?<!\\)$VALIDATOR_PARAMETERS_SEPARATOR""".toRegex()

class TemplateParser(source: String) {

  private val parsedElements by lazy { findElements(source) }

  private inline fun <reified T : ValidatorTemplateElement> Iterable<ValidatorTemplateElement>.findIsInstance(): T? =
    find { it is T } as? T

  fun unexpectedWhitespaces() = parsedElements.filterIsInstance<UnexpectedWhitespaces>()

  fun validator() = parsedElements.findIsInstance<Validator>()

  fun validatorId() = parsedElements.findIsInstance<ValidatorId>()

  fun parameters() = parsedElements.filterIsInstance<Parameter>()

  fun matchingElements(): List<ValidatorTemplateElement> = parsedElements

  companion object {
    fun findElements(source: String): List<ValidatorTemplateElement> {
      val elements = mutableListOf<ValidatorTemplateElement>()

      val contentMatchResult: MatchResult? by lazy {
        CONTENT_WITH_SPACES_REGEX.matchEntire(source)
      }

      listOfNotNull(
        contentMatchResult?.groups?.get(WHITE_SPACES_BEFORE_GROUP_NAME),
        contentMatchResult?.groups?.get(WHITE_SPACES_AFTER_GROUP_NAME)
      )
        .filterNot { it.range.isEmpty() }
        .map {
          UnexpectedWhitespaces(
            range = it.range.toTextRange(),
            value = it.value
          )
        }
        .also { elements.addAll(it) }

      contentMatchResult.group(VALIDATOR_GROUP_NAME)
        ?.let {
          Validator(
            range = it.range.toTextRange(),
            value = it.value
          )
        }
        ?.also {
          elements.add(it)
        }

      contentMatchResult
        .group(VALIDATOR_START_MARKER_GROUP_NAME)
        ?.let {
          ValidatorStartMarker(
            range = it.range.toTextRange(),
            value = it.value
          )
        }
        ?.also { elements.add(it) }
      contentMatchResult
        .group(VALIDATOR_CONTENT_GROUP_NAME)
        ?.let {
          ValidatorContent(
            range = it.range.toTextRange(),
            value = it.value
          )
        }
        ?.also {
          elements.add(it)
          elements.addAll(findContentElements(it.value, it.range.startOffset))
        }
      contentMatchResult
        .group(VALIDATOR_END_MARKER_GROUP_NAME)
        ?.let {
          ValidatorEndMarker(
            range = it.range.toTextRange(),
            value = it.value
          )
        }
        ?.also { elements.add(it) }

      return elements.toList()
    }

    fun findContentElements(source: String, offset: Int = 0): List<ValidatorTemplateElement> {
      val elements = mutableListOf<ValidatorTemplateElement>()

      val contentMatchResult = VALIDATOR_CONTENT_TEMPLATE_REGEX.matchEntire(source)
      contentMatchResult
        .group(VALIDATOR_ID_GROUP_NAME)
        ?.let {
          ValidatorId(
            range = it.range.toTextRange(offset),
            value = it.value
          )
        }
        ?.also { elements.add(it) }
      contentMatchResult
        .group(VALIDATOR_PARAMETERS_MARKER_GROUP_NAME)
        ?.let {
          ParametersStartMarker(
            range = it.range.toTextRange(offset),
            value = it.value
          )
        }
        ?.also {
          elements.add(it)
        }
      contentMatchResult
        .group(VALIDATOR_PARAMETERS_GROUP_NAME)
        ?.let {
          Parameters(
            range = it.range.toTextRange(offset),
            value = it.value
          )
        }
        ?.also {
          elements.add(it)
          elements.addAll(findParametersElements(it.value, it.range.startOffset))
        }

      return elements
    }

    fun findParametersElements(source: String, offset: Int = 0): List<ValidatorTemplateElement> {
      val elements = mutableListOf<ValidatorTemplateElement>()

      var currentRangeIndex = 0
      var currentParameterIndex = 0
      VALIDATOR_PARAMETERS_REGEX.findAll(source)
        .flatMap { separatorMatcherResult ->

          val separatorRange = separatorMatcherResult.range
          val parametersSeparator = ParametersSeparator(
            range = separatorRange.toTextRange(offset),
            value = separatorMatcherResult.value
          )
          val parameterRange = currentRangeIndex until separatorRange.first
          currentRangeIndex = separatorRange.last + 1
          sequenceOf(
            Parameter(
              range = parameterRange.toTextRange(offset),
              value = source.substring(parameterRange),
              index = currentParameterIndex++
            ),
            parametersSeparator
          )
        }
        .toList()
        .let {
          val lastIndex = source.lastIndex
          val lastChar = source.lastOrNull()
          when {
            currentRangeIndex <= lastIndex -> {
              val parameterRange = currentRangeIndex..lastIndex
              it + Parameter(
                range = parameterRange.toTextRange(offset),
                value = source.substring(parameterRange),
                index = currentParameterIndex++
              )
            }
            lastChar == null || lastChar == VALIDATOR_PARAMETERS_START_MARKER || lastChar == VALIDATOR_PARAMETERS_SEPARATOR -> {
              it + Parameter(
                range = (currentRangeIndex until currentRangeIndex).toTextRange(offset),
                value = "",
                index = currentParameterIndex++
              )
            }
            else -> {
              it
            }
          }
        }
        .also {
          elements.addAll(it)
        }

      return elements
    }

    private fun MatchResult?.group(name: String) = this?.groups?.get(name)
  }
}

sealed class ValidatorTemplateElement(
  open val range: TextRange,
  open val value: String
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ValidatorTemplateElement

    if (range != other.range) return false
    if (value != other.value) return false

    return true
  }

  override fun hashCode(): Int {
    var result = range.hashCode()
    result = 31 * result + value.hashCode()
    return result
  }

  override fun toString(): String {
    return "${this::class.simpleName}(range=$range, value='$value')"
  }
}

class UnexpectedWhitespaces(range: TextRange, value: String) : ValidatorTemplateElement(range, value)
class Validator(range: TextRange, value: String) : ValidatorTemplateElement(range, value)
class ValidatorStartMarker(range: TextRange, value: String) : ValidatorTemplateElement(range, value)
class ValidatorEndMarker(range: TextRange, value: String) : ValidatorTemplateElement(range, value)
class ValidatorContent(range: TextRange, value: String) : ValidatorTemplateElement(range, value)
class ValidatorId(range: TextRange, value: String) : ValidatorTemplateElement(range, value)
class Parameters(range: TextRange, value: String) : ValidatorTemplateElement(range, value)
class ParametersStartMarker(range: TextRange, value: String) : ValidatorTemplateElement(range, value)
class ParametersSeparator(range: TextRange, value: String) : ValidatorTemplateElement(range, value)
class Parameter(
  override val range: TextRange,
  override val value: String,
  val index: Int
) : ValidatorTemplateElement(range, value) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    if (!super.equals(other)) return false

    other as Parameter

    if (range != other.range) return false
    if (value != other.value) return false
    if (index != other.index) return false

    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode()
    result = 31 * result + range.hashCode()
    result = 31 * result + value.hashCode()
    result = 31 * result + index
    return result
  }

  override fun toString(): String {
    return "Parameter(range=$range, value='$value', index=$index)"
  }
}

private fun IntRange.toTextRange(offset: Int) =
  this.toTextRange().shiftRight(offset)

private fun TextRange.adjustToElement(element: ValidatorTemplateElement) =
  this.shiftRight(element.range.startOffset)
