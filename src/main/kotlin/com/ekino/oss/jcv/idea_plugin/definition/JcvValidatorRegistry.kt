package com.ekino.oss.jcv.idea_plugin.definition

import com.ekino.oss.jcv.idea_plugin.definition.impl.JCV_CORE_VALIDATORS
import com.ekino.oss.jcv.idea_plugin.definition.impl.JCV_DB_VALIDATORS
import com.ekino.oss.jcv.idea_plugin.service.JcvDefinitionsCache

private val JCV_VALIDATORS = JCV_CORE_VALIDATORS + JCV_DB_VALIDATORS

object JcvValidatorRegistry {

  fun getAllValidators(): List<JcvValidatorDefinition> = JcvDefinitionsCache.getAllValidators() + JCV_VALIDATORS

  fun getLibraryOrigins(): Set<LibraryOrigin> = getAllValidators().mapNotNull { it.origin as? LibraryOrigin }.toSet()

  fun findById(id: String) = getAllValidators().find { it.id == id }
}