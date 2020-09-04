package com.ekino.oss.jcv.idea_plugin.suggestion

import com.intellij.json.psi.JsonValue

interface ValueToValidatorSuggestion {
  fun suggest(value: JsonValue): List<String>
}
