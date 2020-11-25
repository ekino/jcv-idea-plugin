package com.ekino.oss.jcv.idea_plugin.utils

import com.intellij.openapi.util.TextRange

fun IntRange.toTextRange(): TextRange {
  val startOffset = first
  val endOffset = if (isEmpty()) {
    startOffset
  } else {
    last + 1
  }
  return TextRange(
    startOffset,
    endOffset
  )
}

fun TextRange.shiftStart(delta: Int) = TextRange(startOffset + delta, endOffset)
