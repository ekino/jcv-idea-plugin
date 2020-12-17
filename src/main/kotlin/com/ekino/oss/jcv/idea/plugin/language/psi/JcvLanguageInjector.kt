package com.ekino.oss.jcv.idea.plugin.language.psi

import com.ekino.oss.jcv.idea.plugin.language.JcvLanguage
import com.ekino.oss.jcv.idea.plugin.language.JcvUtil
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiLanguageInjectionHost

class JcvLanguageInjector : LanguageInjector {
  override fun getLanguagesToInject(host: PsiLanguageInjectionHost, injectionPlacesRegistrar: InjectedLanguagePlaces) {
    val jsonStringLiteral = (host as? JsonStringLiteral)
      ?.takeIf { JcvUtil.isJcvValidatorCandidate(it) }
      ?: return

    injectionPlacesRegistrar.addPlace(
      JcvLanguage,
      TextRange(
        jsonStringLiteral.textFragments.firstOrNull()?.first?.startOffset ?: 0,
        jsonStringLiteral.textFragments.lastOrNull()?.first?.endOffset ?: jsonStringLiteral.textLength
      ),
      null,
      null
    )
  }
}
