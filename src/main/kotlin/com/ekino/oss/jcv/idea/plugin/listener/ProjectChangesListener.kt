package com.ekino.oss.jcv.idea.plugin.listener

import com.ekino.oss.jcv.idea.plugin.service.JcvLibraryCache
import com.intellij.openapi.roots.ModuleRootEvent
import com.intellij.openapi.roots.ModuleRootListener

/**
 * @see <a href="https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000018699/comments/360000016240">
 *   Listener for module library changes
 *   </a>
 */
class ProjectChangesListener : ModuleRootListener {

  override fun rootsChanged(event: ModuleRootEvent) {
    JcvLibraryCache.refreshCache(event.project)
  }
}
