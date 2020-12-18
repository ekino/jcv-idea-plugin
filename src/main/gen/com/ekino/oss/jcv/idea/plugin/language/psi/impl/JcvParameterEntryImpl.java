// This is a generated file. Not intended for manual editing.
package com.ekino.oss.jcv.idea.plugin.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.ekino.oss.jcv.idea.plugin.language.psi.JcvTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.ekino.oss.jcv.idea.plugin.language.psi.*;

public class JcvParameterEntryImpl extends ASTWrapperPsiElement implements JcvParameterEntry {

  public JcvParameterEntryImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JcvVisitor visitor) {
    visitor.visitParameterEntry(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JcvVisitor) accept((JcvVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public Integer getIndex() {
    return JcvPsiImplUtil.getIndex(this);
  }

  @Override
  @Nullable
  public PsiElement getSeparator() {
    return JcvPsiImplUtil.getSeparator(this);
  }

  @Override
  @Nullable
  public PsiElement getParameterValue() {
    return JcvPsiImplUtil.getParameterValue(this);
  }

}
