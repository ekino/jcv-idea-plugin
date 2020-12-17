package com.ekino.oss.jcv.idea.plugin.definition.custom

import com.ekino.oss.jcv.idea.plugin.definition.JcvValidatorRegistry
import com.ekino.oss.jcv.idea.plugin.language.JcvIcons
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonProperty
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType

class JcvDefinitionLineMarkerProvider : RelatedItemLineMarkerProvider() {

  @Suppress("ReturnCount")
  override fun collectNavigationMarkers(
    element: PsiElement,
    result: MutableCollection<in RelatedItemLineMarkerInfo<PsiElement>>
  ) {
    val validatorId = (element as? com.ekino.oss.jcv.idea.plugin.language.psi.JcvValidator)
      ?.validatorId
      ?: return
    val definitionsFile = JcvValidatorRegistry.findById(validatorId)
      ?.let { it.origin as? com.ekino.oss.jcv.idea.plugin.definition.ProjectOrigin }
      ?.file
      ?.let { PsiManager.getInstance(element.project).findFile(it) as? JsonFile }
      ?: return

    val jcvDefinitionElement = PsiTreeUtil.findChildrenOfType(definitionsFile, JsonProperty::class.java)
      .find {
        it.name == "id" &&
          it.containingFile.name == ValidatorDescriptionsResolver.definitionsFileName() &&
          it.parentOfType<JsonProperty>()?.name == "validators"
      }
      ?: return
    NavigationGutterIconBuilder.create(JcvIcons.FILE)
      .setTarget(jcvDefinitionElement.value)
      .setTooltipText("Navigate to Jcv validator definition")
      .createLineMarkerInfo(element.validatorIdElement!!)
      .also { result.add(it) }
  }
}
