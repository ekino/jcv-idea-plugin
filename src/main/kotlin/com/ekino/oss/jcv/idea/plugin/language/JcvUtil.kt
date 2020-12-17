package com.ekino.oss.jcv.idea.plugin.language

import com.ekino.oss.jcv.idea.plugin.language.psi.JcvParameterEntry
import com.intellij.json.psi.JsonPsiUtil
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType

private val JCV_CANDIDATE_MATCHER = """^\s*\{#.*""".toRegex()

object JcvUtil {

  @JvmStatic
  fun isJcvValidatorCandidate(element: PsiElement): Boolean {
    return (element as? JsonStringLiteral)
      ?.takeIf { JsonPsiUtil.isPropertyValue(it) || JsonPsiUtil.isArrayElement(it) }
      ?.let {
        it.textFragments
          .firstOrNull()
          ?.second
          ?.let { textValue ->
            isJcvValidatorCandidate(textValue)
          }
      }
      ?: false
  }

  @JvmStatic
  fun isJcvValidatorCandidate(stringLiteralValue: String): Boolean = stringLiteralValue.matches(JCV_CANDIDATE_MATCHER)

  @JvmStatic
  fun getParameterEntry(parameter: PsiElement) = parameter.parentOfType<JcvParameterEntry>()
}
