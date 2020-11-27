// This is a generated file. Not intended for manual editing.
package com.ekino.oss.jcv.idea_plugin.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.navigation.ItemPresentation;

public interface JcvValidator extends JcvNamedElement {

  @Nullable
  JcvParameters getParameters();

  @Nullable
  String getValidatorId();

  @Nullable
  PsiElement getValidatorIdElement();

  @Nullable
  String getName();

  @NotNull
  PsiElement setName(@NotNull String newName);

  @Nullable
  PsiElement getNameIdentifier();

  int getTextOffset();

  @NotNull
  ItemPresentation getPresentation();

}
