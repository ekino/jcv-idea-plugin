package com.ekino.oss.jcv.idea_plugin.definition

import com.intellij.openapi.vfs.VirtualFile
import java.net.URI

interface JcvValidatorDefinition {
  val id: String
  val parameters: List<ParameterDefinition>
  val origin: JcvValidatorOrigin
}

interface ParameterDefinition {
  val description: String?
  val required: Boolean
  val suggestedValues: List<String>
}

interface JcvValidatorOrigin {
  val displayName: String
}

interface DocumentationUriProvider {
  val documentation: URI
}

class LibraryOrigin(
  override val displayName: String,
  val artifactId: String? = null,
  val groupId: String? = null,
  override val documentation: URI
) : JcvValidatorOrigin, DocumentationUriProvider {
  fun dependencyPattern(): String? = listOfNotNull(groupId, artifactId)
    .filter { it.isNotBlank() }
    .takeIf { it.isNotEmpty() }
    ?.joinToString(":")

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as LibraryOrigin

    if (artifactId != other.artifactId) return false
    if (groupId != other.groupId) return false

    return true
  }

  override fun hashCode(): Int {
    var result = artifactId?.hashCode() ?: 0
    result = 31 * result + (groupId?.hashCode() ?: 0)
    return result
  }
}

class ProjectOrigin(val file: VirtualFile) : JcvValidatorOrigin {
  override val displayName: String = "Project"
}
