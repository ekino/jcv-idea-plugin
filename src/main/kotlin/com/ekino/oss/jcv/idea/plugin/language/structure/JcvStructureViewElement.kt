package com.ekino.oss.jcv.idea.plugin.language.structure

import com.ekino.oss.jcv.idea.plugin.language.psi.JcvFile
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.util.PsiTreeUtil

class JcvStructureViewElement(
  private val myElement: NavigatablePsiElement
) : StructureViewTreeElement, SortableTreeElement {

  override fun getValue(): Any = myElement

  override fun navigate(requestFocus: Boolean) {
    myElement.navigate(requestFocus)
  }

  override fun canNavigate(): Boolean = myElement.canNavigate()

  override fun canNavigateToSource(): Boolean = myElement.canNavigateToSource()

  override fun getAlphaSortKey(): String = myElement.name.orEmpty()

  override fun getPresentation(): ItemPresentation = myElement.presentation ?: PresentationData()

  override fun getChildren(): Array<TreeElement> = (myElement as? JcvFile)
    ?.let { jcvFile ->
      PsiTreeUtil.getChildrenOfTypeAsList(jcvFile, com.ekino.oss.jcv.idea.plugin.language.psi.JcvValidator::class.java)
        .map { JcvStructureViewElement(it as NavigatablePsiElement) }
        .toTypedArray()
    }
    ?: emptyArray()
}
