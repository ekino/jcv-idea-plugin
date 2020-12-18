package com.ekino.oss.jcv.idea.plugin.definition.impl

import com.ekino.oss.jcv.idea.plugin.definition.DocumentationUriProvider
import com.ekino.oss.jcv.idea.plugin.definition.JcvValidatorDefinition
import com.ekino.oss.jcv.idea.plugin.definition.LibraryOrigin
import com.ekino.oss.jcv.idea.plugin.definition.ParameterDefinition
import java.net.URI

private const val EKINO_JCV_DB_LIBRARY_GROUP_ID = "com.ekino.oss.jcv-db"

private val JCV_DB_BASE_URI = URI("https://github.com/ekino/jcv-db/")
private val JCV_DB_GLOBAL_DOCUMENTATION = JCV_DB_BASE_URI.resolve("wiki")
private val jcvDbCoreOrigin = LibraryOrigin(
  displayName = "JCV-DB Core",
  artifactId = "jcv-db-core",
  groupId = EKINO_JCV_DB_LIBRARY_GROUP_ID,
  documentation = JCV_DB_GLOBAL_DOCUMENTATION
)
private val jcvDbMongoOrigin = LibraryOrigin(
  displayName = "JCV-DB Mongo",
  artifactId = "jcv-db-mongo",
  groupId = EKINO_JCV_DB_LIBRARY_GROUP_ID,
  documentation = JCV_DB_GLOBAL_DOCUMENTATION
)
private val JCV_DB_VALIDATORS_DOCUMENTATION = JCV_DB_GLOBAL_DOCUMENTATION.resolve("Validators")

internal class JcvDbValidatorDefinition(
  override val id: String,
  override val origin: LibraryOrigin
) : JcvValidatorDefinition,
  DocumentationUriProvider {

  override val parameters: List<ParameterDefinition>
    get() = emptyList()

  override val documentation: URI = JCV_DB_VALIDATORS_DOCUMENTATION.resolve("#$id")
}

val JCV_DB_VALIDATORS: List<JcvValidatorDefinition> by lazy {
  listOf(
    JcvDbValidatorDefinition(
      id = "json_object",
      origin = jcvDbCoreOrigin
    ),
    JcvDbValidatorDefinition(
      id = "json_array",
      origin = jcvDbCoreOrigin
    ),
    JcvDbValidatorDefinition(
      id = "mongo_id",
      origin = jcvDbMongoOrigin
    )
  )
}
