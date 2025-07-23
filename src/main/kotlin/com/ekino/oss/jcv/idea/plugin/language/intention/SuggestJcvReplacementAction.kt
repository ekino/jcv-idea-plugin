package com.ekino.oss.jcv.idea.plugin.language.intention

import com.ekino.oss.jcv.idea.plugin.definition.findModule
import com.ekino.oss.jcv.idea.plugin.language.JcvIcons
import com.intellij.icons.AllIcons
import com.intellij.json.JsonLanguage
import com.intellij.json.psi.JsonElementGenerator
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonValue
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType

class SuggestJcvReplacementAction : AnAction() {

  override fun update(e: AnActionEvent) {
    e.presentation.apply {
      isEnabled = e.project != null && e.getEditor() != null
      icon = JcvIcons.FILE
    }
  }

  override fun actionPerformed(e: AnActionEvent) {

    val jsonValueElements = findCurrentJsonValues(e)
    val module = jsonValueElements.firstOrNull()?.findModule()
    val suggestions = findReplacementSuggestions(module, jsonValueElements)

    val actions = suggestions
      .groupBy { it.category }
      .flatMap { (category, suggestions) ->
        suggestions.map {
          it.toReplacementsAction(jsonValueElements)
        }
          .let { actions ->
            if (category != null) {
              actions.wrap(Separator.create(category.title), Separator.create())
            } else {
              actions
            }
          }
      }

    val actionGroupMenu = ActionManager.getInstance().createActionPopupMenu(
      ActionPlaces.ACTION_PLACE_QUICK_LIST_POPUP_ACTION,
      object : ActionGroup() {
        override fun update(e: AnActionEvent) {
          super.update(e)
          e.presentation.apply {
            icon = AllIcons.Actions.SuggestedRefactoringBulb
          }
        }

        override fun getChildren(e: AnActionEvent?): Array<AnAction> = actions.toTypedArray()
      }
        .also {
          it.isPopup = true
        }
    )

    val dataContext = e.dataContext

    val singleJsonPropertyElt = jsonValueElements.singleOrNull()?.parentOfType<JsonProperty>()
    val title = when {
      singleJsonPropertyElt != null -> "\"${singleJsonPropertyElt.name}\" field's value replacement suggestions"
      else -> "JCV Replacement Suggestions"
    }
    JBPopupFactory.getInstance().createActionGroupPopup(
      title,
      actionGroupMenu.actionGroup,
      dataContext,
      JBPopupFactory.ActionSelectionAid.ALPHA_NUMBERING,
      false,
    )
      .showInBestPositionFor(dataContext)
  }

  private fun findCurrentJsonValues(e: AnActionEvent): List<JsonValue> {
    val psiFile =
      e.getData(CommonDataKeys.PSI_FILE)?.takeIf { it.language == JsonLanguage.INSTANCE } ?: return emptyList()
    return e.getEditor()
      ?.caretModel
      ?.allCarets
      ?.mapNotNull { caret ->
        PsiTreeUtil.findElementOfClassAtOffset(psiFile, caret.offset, JsonValue::class.java, false)
      }
      .orEmpty()
  }
}

private fun AnActionEvent.getEditor(): Editor? = getData(CommonDataKeys.EDITOR)

private fun <T> List<T>.wrap(prefix: T? = null, suffix: T? = null): List<T> {
  val newList = mutableListOf<T>()
  prefix?.also { newList.add(it) }
  newList.addAll(this)
  suffix?.also { newList.add(it) }
  return newList
}

private const val MAX_TEXT_LENGTH = 100

private fun JcvValidatorSuggestion.toReplacementsAction(elementsToReplace: List<JsonValue>): AnAction {
  val replacement = this.asText()

  return object : AnAction() {

    override fun update(e: AnActionEvent) {
      super.update(e)
      e.presentation.apply {
        setText(replacement.limitLength(MAX_TEXT_LENGTH), false)
        icon = AllIcons.Actions.SuggestedRefactoringBulb
      }
    }

    override fun actionPerformed(e: AnActionEvent) {
      val project = e.project ?: return
      WriteCommandAction.runWriteCommandAction(project) {
        elementsToReplace.forEach {
          it.replace(JsonElementGenerator(project).createStringLiteral(replacement))
        }
      }
    }
  }
}

private fun String.limitLength(maxLength: Int, ellipsis: String = Typography.ellipsis.toString()): String {
  return when {
    this.length > maxLength -> this.substring(0, maxLength - ellipsis.length) + ellipsis
    else -> this
  }
}
