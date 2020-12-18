// This is a generated file. Not intended for manual editing.
package com.ekino.oss.jcv.idea.plugin.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.ekino.oss.jcv.idea.plugin.language.psi.JcvTypes.*;
import com.ekino.oss.jcv.idea.plugin.language.psi.*;
import com.intellij.navigation.ItemPresentation;

public class JcvValidatorImpl extends JcvNamedElementImpl implements JcvValidator {

  public JcvValidatorImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JcvVisitor visitor) {
    visitor.visitValidator(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JcvVisitor) accept((JcvVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JcvParameters getParameters() {
    return findChildByClass(JcvParameters.class);
  }

  @Override
  @Nullable
  public String getValidatorId() {
    return JcvPsiImplUtil.getValidatorId(this);
  }

  @Override
  @Nullable
  public PsiElement getValidatorIdElement() {
    return JcvPsiImplUtil.getValidatorIdElement(this);
  }

  @Override
  @Nullable
  public String getName() {
    return JcvPsiImplUtil.getName(this);
  }

  @Override
  @NotNull
  public PsiElement setName(@NotNull String newName) {
    return JcvPsiImplUtil.setName(this, newName);
  }

  @Override
  @Nullable
  public PsiElement getNameIdentifier() {
    return JcvPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  public int getTextOffset() {
    return JcvPsiImplUtil.getTextOffset(this);
  }

  @Override
  @NotNull
  public ItemPresentation getPresentation() {
    return JcvPsiImplUtil.getPresentation(this);
  }

}
