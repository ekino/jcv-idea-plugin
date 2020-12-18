package com.ekino.oss.jcv.idea.plugin.language;

import com.intellij.lexer.FlexLexer;
import com.ekino.oss.jcv.idea.plugin.language.psi.JcvTypes;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

%%

%class JcvLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]

VALIDATOR_START_MARKER="{#"
VALIDATOR_END_MARKER="#}"

ID_CHARACTER=[a-zA-Z0-9\-_.]
PARAMETERS_START_MARKER=[:]
PARAMETER_SEPARATOR=[;]
PARAMETER_CHARACTER=[^;\n\f] | "\\".

%state VALIDATOR
%state PARAMETERS
%state PARAMETER

%%

<YYINITIAL> {
    {VALIDATOR_START_MARKER} { yybegin(VALIDATOR); return JcvTypes.VALIDATOR_START_MARKER; }
    {WhiteSpace}+ { return TokenType.WHITE_SPACE; }
}

<VALIDATOR> {
    {VALIDATOR_END_MARKER} { yybegin(YYINITIAL); return JcvTypes.VALIDATOR_END_MARKER; }
    {VALIDATOR_START_MARKER} { yybegin(VALIDATOR); return JcvTypes.VALIDATOR_START_MARKER; }
    {ID_CHARACTER}+ { yybegin(VALIDATOR); return JcvTypes.VALIDATOR_ID; }
    {PARAMETERS_START_MARKER} { yybegin(PARAMETERS); return JcvTypes.PARAMETERS_START_MARKER; }
    {WhiteSpace}+ { return TokenType.WHITE_SPACE; }
}

<PARAMETERS> {
    {PARAMETER_SEPARATOR} { yybegin(PARAMETERS); return JcvTypes.PARAMETER_SEPARATOR; }
    {VALIDATOR_END_MARKER} { yybegin(YYINITIAL); return JcvTypes.VALIDATOR_END_MARKER; }
    {PARAMETER_CHARACTER} { yybegin(PARAMETER); }
}

<PARAMETER> {
    {PARAMETER_SEPARATOR} { yypushback(1); yybegin(PARAMETERS); return JcvTypes.PARAMETER; }
    {VALIDATOR_END_MARKER} { yypushback(2); yybegin(VALIDATOR); return JcvTypes.PARAMETER; }
    {LineTerminator} { yypushback(1); yybegin(YYINITIAL); return JcvTypes.PARAMETER; }
    <<EOF>> { yybegin(YYINITIAL); return JcvTypes.PARAMETER; }
    {PARAMETER_CHARACTER} {}
}

[^] { return TokenType.BAD_CHARACTER; }
