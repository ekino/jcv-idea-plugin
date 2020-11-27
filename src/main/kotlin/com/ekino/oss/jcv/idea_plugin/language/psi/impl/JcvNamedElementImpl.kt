package com.ekino.oss.jcv.idea_plugin.language.psi.impl

import com.ekino.oss.jcv.idea_plugin.language.psi.JcvNamedElement
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class JcvNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), JcvNamedElement
