// This is a generated file. Not intended for manual editing.
package com.ekino.oss.jcv.idea.plugin.language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.ekino.oss.jcv.idea.plugin.language.psi.impl.*;

public interface JcvTypes {

  IElementType PARAMETERS = new JcvElementType("PARAMETERS");
  IElementType PARAMETER_ENTRY = new JcvElementType("PARAMETER_ENTRY");
  IElementType VALIDATOR = new JcvElementType("VALIDATOR");

  IElementType PARAMETER = new JcvTokenType("PARAMETER");
  IElementType PARAMETERS_START_MARKER = new JcvTokenType("PARAMETERS_START_MARKER");
  IElementType PARAMETER_SEPARATOR = new JcvTokenType("PARAMETER_SEPARATOR");
  IElementType VALIDATOR_END_MARKER = new JcvTokenType("VALIDATOR_END_MARKER");
  IElementType VALIDATOR_ID = new JcvTokenType("VALIDATOR_ID");
  IElementType VALIDATOR_START_MARKER = new JcvTokenType("VALIDATOR_START_MARKER");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == PARAMETERS) {
        return new JcvParametersImpl(node);
      }
      else if (type == PARAMETER_ENTRY) {
        return new JcvParameterEntryImpl(node);
      }
      else if (type == VALIDATOR) {
        return new JcvValidatorImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
