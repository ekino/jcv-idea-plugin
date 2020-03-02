package com.ekino.oss.jcv.idea_plugin.autocomplete

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider

class JcvTemplateProvider : DefaultLiveTemplatesProvider {

  override fun getDefaultLiveTemplateFiles(): Array<String> =
    arrayOf("jcv", "jcv-db")

  override fun getHiddenLiveTemplateFiles(): Array<String>? = null
}
