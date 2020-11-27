package com.ekino.oss.jcv.idea_plugin.listener

import com.ekino.oss.jcv.idea_plugin.definition.custom.ValidatorDescriptionsResolver
import com.ekino.oss.jcv.idea_plugin.service.JcvDefinitionsCache
import com.ekino.oss.jcv.idea_plugin.service.JcvLibraryCache
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

/**
 * @see <a href="https://intellij-support.jetbrains.com/hc/en-us/community/posts/360002476840/comments/360000302299">How to auto start(initialize) plugin on project loaded?</a>
 */
class PostActivityStartupListener : StartupActivity.DumbAware {
  override fun runActivity(project: Project) {
    JcvLibraryCache.refreshCache(project)

    JcvDefinitionsCache.refreshCache(project)

    project.messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
      override fun after(events: MutableList<out VFileEvent>) {
        events
          .find { it.file?.name == ValidatorDescriptionsResolver.definitionsFileName() }
          ?.also { JcvDefinitionsCache.refreshCache(project) }
      }
    })
  }
}