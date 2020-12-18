// This is a generated file. Not intended for manual editing.
package com.ekino.oss.jcv.idea.plugin.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import kotlin.Pair;

public interface JcvParameters extends PsiElement {

  @NotNull
  List<JcvParameterEntry> getParameterEntryList();

  @NotNull
  List<Pair<Integer, JcvParameterEntry>> getIndexedParameters();

}
