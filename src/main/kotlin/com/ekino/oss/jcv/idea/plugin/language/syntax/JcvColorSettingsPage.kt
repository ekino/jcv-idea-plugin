package com.ekino.oss.jcv.idea.plugin.language.syntax

import com.ekino.oss.jcv.idea.plugin.language.JcvIcons
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

private val DESCRIPTORS = arrayOf(
  AttributesDescriptor("Markers", JcvSyntaxHighlighter.VALIDATOR_MARKERS),
  AttributesDescriptor("Validator id", JcvSyntaxHighlighter.VALIDATOR_ID),
  AttributesDescriptor("Validator Parameter", JcvSyntaxHighlighter.VALIDATOR_PARAMETER),
  AttributesDescriptor("Bad Value", JcvSyntaxHighlighter.BAD_CHARACTER)
)

class JcvColorSettingsPage : ColorSettingsPage {
  override fun getIcon(): Icon = JcvIcons.FILE

  override fun getHighlighter(): SyntaxHighlighter = JcvSyntaxHighlighter()

  override fun getDemoText(): String = "{#date_time_format:iso_instant;fr-FR#}"

  override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? = null

  override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

  override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

  override fun getDisplayName(): String = "JCV"
}
