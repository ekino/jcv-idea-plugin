package com.ekino.oss.jcv.idea_plugin.language.documentation

import com.ekino.oss.jcv.idea_plugin.definition.DocumentationUriProvider
import com.ekino.oss.jcv.idea_plugin.definition.JcvValidatorRegistry
import com.ekino.oss.jcv.idea_plugin.language.psi.JcvValidator
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement

class JcvValidatorDocumentationProvider : AbstractDocumentationProvider() {

  override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
    return (element as? JcvValidator)
      ?.let { generateDocumentation(it) }
  }

  override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
    return (element as? JcvValidator)
      ?.let { generateDocumentation(it) }
  }

  private fun generateDocumentation(element: JcvValidator): String {
    val validatorId = element.validatorId
    val validatorDef = validatorId?.let { JcvValidatorRegistry.findById(it) }

    //language=HTML
    val docTitleHtml = (
      validatorId
        ?.let { id ->
          val originDoc = validatorDef?.origin?.let { origin ->
            val displayName = origin.displayName
            when (origin) {
              is DocumentationUriProvider -> {
                """<a href="${origin.documentation}">$displayName</a>"""
              }
              else -> displayName
            }
          }
            ?.let { " ($it)" }
            .orEmpty()

          "<b>$id</b>$originDoc"
        }
        ?: "<i>empty</i>"
      )
      .let { "<p>$it</p>" }


    //language=HTML
    val parametersHtml = getParametersValues(element)
      .takeIf { it.isNotEmpty() }
      ?.let { parameters ->
        parameters.mapIndexed { index, paramValue ->
          validatorDef
            ?.parameters
            ?.getOrNull(index)
            ?.description
            ?.let { "$it: " }
            .orEmpty() + "<b>$paramValue</b>"
        }
          .joinToString("\n") { "<li>$it</li>" }
      }
      ?.let {
        """
        <ol>
        $it
        </ol>
        """.trimIndent()
      }
      .orEmpty()

    //language=HTML
    val externalDocumentationHtml = (validatorDef as? DocumentationUriProvider)?.documentation
      ?.let { uri ->
        """
        <br>
        <p>See also: <a href="$uri">$validatorId - Official documentation</a></p>
        """.trimIndent()
      }
      .orEmpty()

    //language=HTML
    return "$docTitleHtml$parametersHtml$externalDocumentationHtml"
  }

  private fun getParametersValues(element: JcvValidator) = element
    .parameters
    ?.indexedParameters
    ?.map { (_, entry) -> entry?.parameterValue?.text.orEmpty() }
    .orEmpty()
}
