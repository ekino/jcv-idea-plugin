package com.ekino.oss.jcv.idea.plugin.language.psi.impl

import com.ekino.oss.jcv.idea.plugin.language.JcvIcons
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvElementFactory
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvParameterEntry
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvParameters
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvTypes.PARAMETER
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvTypes.PARAMETER_SEPARATOR
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvTypes.VALIDATOR_ID
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvValidator
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.collectDescendantsOfType
import com.intellij.psi.util.elementType
import com.intellij.psi.util.findDescendantOfType
import com.intellij.psi.util.parentOfType
import javax.swing.Icon

@Suppress("TooManyFunctions")
object JcvPsiImplUtil {
  @JvmStatic
  fun getValidatorId(element: JcvValidator): String? = element.findValidatorIdElement()?.text

  @JvmStatic
  fun getValidatorIdElement(element: JcvValidator): PsiElement? = element.findValidatorIdElement()

  @JvmStatic
  fun getName(element: JcvValidator): String? = getValidatorId(element)

  @JvmStatic
  fun setName(element: JcvValidator, newName: String): PsiElement {
    element.findValidatorIdElement()
      ?.also { oldValidatorIdElt ->
        JcvElementFactory.createValidator(element.project, newName)
          ?.findValidatorIdElement()
          ?.also { newValidatorIdElt ->
            element.node.replaceChild(oldValidatorIdElt.node, newValidatorIdElt.node)
          }
      }
    return element
  }

  @JvmStatic
  fun getNameIdentifier(element: JcvValidator) = element.findValidatorIdElement()

  /**
   * VERY IMPORTANT for the "name identifier" token relative position in the PsiElement!
   *
   * @see com.intellij.psi.PsiNameIdentifierOwner
   */
  @JvmStatic
  fun getTextOffset(element: JcvValidator) = element.findValidatorIdElement()?.textOffset ?: 0

  @JvmStatic
  fun getPresentation(element: JcvValidator) = object : ItemPresentation {
    override fun getPresentableText(): String? = element.validatorId

    override fun getLocationString(): String? = element.parameters
      ?.indexedParameters
      ?.map { it.second?.parameterValue?.text.orEmpty() }
      ?.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }

    override fun getIcon(unused: Boolean): Icon = JcvIcons.FILE
  }

  @JvmStatic
  fun getParameterValue(element: JcvParameterEntry) =
    element.findDescendantOfType<PsiElement> { it.elementType == PARAMETER }

  @JvmStatic
  fun getIndexedParameters(element: JcvParameters): List<Pair<Int, JcvParameterEntry?>> {
    val paramEntries = element.collectDescendantsOfType<JcvParameterEntry>()

    // Manage the following use case : "{#validator:;param2#}" -> param 2 should be index 1
    val firstParamDelta = paramEntries.firstOrNull()?.separator?.let { listOf(null) } ?: emptyList()

    return (firstParamDelta + paramEntries)
      .mapIndexed { index, entry -> index to entry }
  }

  @JvmStatic
  fun getIndex(element: JcvParameterEntry): Int? =
    element.parentOfType<JcvParameters>()
      ?.let { getIndexedParameters(it) }
      ?.find { (_, entry) -> entry == element }
      ?.first

  @JvmStatic
  fun getSeparator(element: JcvParameterEntry) =
    element.findDescendantOfType<PsiElement> {
      it.elementType == PARAMETER_SEPARATOR
    }

  private fun PsiElement.findValidatorIdElement() =
    findDescendantOfType<PsiElement> { it.elementType == VALIDATOR_ID }
}
