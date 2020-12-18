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
import kotlin.Pair;

public class JcvParametersImpl extends ASTWrapperPsiElement implements JcvParameters {

  public JcvParametersImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JcvVisitor visitor) {
    visitor.visitParameters(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JcvVisitor) accept((JcvVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JcvParameterEntry> getParameterEntryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JcvParameterEntry.class);
  }

  @Override
  @NotNull
  public List<Pair<Integer, JcvParameterEntry>> getIndexedParameters() {
    return JcvPsiImplUtil.getIndexedParameters(this);
  }

}
