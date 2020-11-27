// This is a generated file. Not intended for manual editing.
package com.ekino.oss.jcv.idea_plugin.language.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class JcvVisitor extends PsiElementVisitor {

  public void visitParameterEntry(@NotNull JcvParameterEntry o) {
    visitPsiElement(o);
  }

  public void visitParameters(@NotNull JcvParameters o) {
    visitPsiElement(o);
  }

  public void visitValidator(@NotNull JcvValidator o) {
    visitNamedElement(o);
  }

  public void visitNamedElement(@NotNull JcvNamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
