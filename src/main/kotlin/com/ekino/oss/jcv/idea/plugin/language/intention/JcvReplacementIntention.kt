package com.ekino.oss.jcv.idea.plugin.language.intention

import com.ekino.oss.jcv.idea.plugin.language.JcvBundle
import com.ekino.oss.jcv.idea.plugin.language.JcvUtil
import com.intellij.ide.DataManager
import com.intellij.json.psi.JsonPsiUtil
import com.intellij.json.psi.JsonValue
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType

class JcvReplacementIntention : JcvBaseIntentionAction() {

  override fun getFamilyName(): String = JcvBundle.getMessage("jcv.replacement.suggestions.family-name")

  override fun getText(): String = JcvBundle.getMessage("jcv.replacement.suggestions.text")

  override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean =
    element.parentOfType<JsonValue>(withSelf = true)
      ?.takeIf { JsonPsiUtil.isPropertyValue(it) || JsonPsiUtil.isArrayElement(it) }
      ?.takeUnless { JcvUtil.isJcvValidatorCandidate(it) }
      .let { it != null }

  override fun startInWriteAction(): Boolean = false

  override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
    SuggestJcvReplacementAction().also { action ->
      /**
       * @see <a href="https://stackoverflow.com/a/48673746">Get data context</a>
       */
      val dataContext = DataManager.getInstance().getDataContext(editor?.contentComponent)
      action.actionPerformed(
        AnActionEvent.createFromDataContext(
          ActionPlaces.ACTION_PLACE_QUICK_LIST_POPUP_ACTION,
          action.templatePresentation,
          dataContext
        )
      )
    }
  }
}
