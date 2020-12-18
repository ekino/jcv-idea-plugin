// This is a generated file. Not intended for manual editing.
package com.ekino.oss.jcv.idea.plugin.language.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.ekino.oss.jcv.idea.plugin.language.psi.JcvTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class JcvParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return jcvFile(b, l + 1);
  }

  /* ********************************************************** */
  // validator
  static boolean jcvFile(PsiBuilder b, int l) {
    return validator(b, l + 1);
  }

  /* ********************************************************** */
  // PARAMETER | PARAMETER_SEPARATOR PARAMETER?
  public static boolean parameterEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterEntry")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER_ENTRY, "<parameter entry>");
    r = consumeToken(b, PARAMETER);
    if (!r) r = parameterEntry_1(b, l + 1);
    exit_section_(b, l, m, r, false, JcvParser::parameterRecover);
    return r;
  }

  // PARAMETER_SEPARATOR PARAMETER?
  private static boolean parameterEntry_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterEntry_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PARAMETER_SEPARATOR);
    r = r && parameterEntry_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PARAMETER?
  private static boolean parameterEntry_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterEntry_1_1")) return false;
    consumeToken(b, PARAMETER);
    return true;
  }

  /* ********************************************************** */
  // !(VALIDATOR_END_MARKER|PARAMETER_SEPARATOR|PARAMETER|PARAMETERS_START_MARKER)
  static boolean parameterRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !parameterRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // VALIDATOR_END_MARKER|PARAMETER_SEPARATOR|PARAMETER|PARAMETERS_START_MARKER
  private static boolean parameterRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterRecover_0")) return false;
    boolean r;
    r = consumeToken(b, VALIDATOR_END_MARKER);
    if (!r) r = consumeToken(b, PARAMETER_SEPARATOR);
    if (!r) r = consumeToken(b, PARAMETER);
    if (!r) r = consumeToken(b, PARAMETERS_START_MARKER);
    return r;
  }

  /* ********************************************************** */
  // PARAMETERS_START_MARKER parameterEntry*
  public static boolean parameters(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameters")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PARAMETERS, "<parameters>");
    r = consumeToken(b, PARAMETERS_START_MARKER);
    p = r; // pin = 1
    r = r && parameters_1(b, l + 1);
    exit_section_(b, l, m, r, p, JcvParser::parameterRecover);
    return r || p;
  }

  // parameterEntry*
  private static boolean parameters_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameters_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!parameterEntry(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "parameters_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // VALIDATOR_START_MARKER VALIDATOR_ID parameters? VALIDATOR_END_MARKER
  public static boolean validator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "validator")) return false;
    if (!nextTokenIs(b, VALIDATOR_START_MARKER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VALIDATOR, null);
    r = consumeTokens(b, 2, VALIDATOR_START_MARKER, VALIDATOR_ID);
    p = r; // pin = 2
    r = r && report_error_(b, validator_2(b, l + 1));
    r = p && consumeToken(b, VALIDATOR_END_MARKER) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // parameters?
  private static boolean validator_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "validator_2")) return false;
    parameters(b, l + 1);
    return true;
  }

}
