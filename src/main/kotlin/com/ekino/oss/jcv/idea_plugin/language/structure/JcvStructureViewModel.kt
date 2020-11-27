package com.ekino.oss.jcv.idea_plugin.language.structure

import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.psi.PsiFile

class JcvStructureViewModel(
  psiFile: PsiFile
) : StructureViewModelBase(psiFile, JcvStructureViewElement(psiFile)), StructureViewModel.ElementInfoProvider {

  override fun getSorters(): Array<Sorter> = arrayOf(Sorter.ALPHA_SORTER)

  override fun isAlwaysShowsPlus(element: StructureViewTreeElement?): Boolean = false

  override fun isAlwaysLeaf(element: StructureViewTreeElement?): Boolean = false
}
