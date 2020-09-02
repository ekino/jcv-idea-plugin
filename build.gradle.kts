import com.ekino.oss.jcv.idea_plugin.autocomplete.template_generator.TemplateGeneratorTask
import org.jetbrains.intellij.tasks.PublishTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.72"
  id("org.jetbrains.intellij") version "0.4.21"
}

intellij {
  version = "IC-2020.1" //IntelliJ IDEA 2020.1 dependency; for a full list of IntelliJ IDEA releases please see https://www.jetbrains.com/intellij-repository/releases
  pluginName = "JCV"
  updateSinceUntilBuild = false //Disables updating since-build attribute in plugin.xml
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("com.willowtreeapps.assertk:assertk-jvm:${property("assertk-jvm.version")}")
  testImplementation("org.junit.jupiter:junit-jupiter:${property("junit.version")}")
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
  }

  withType<Test> {
    useJUnitPlatform()
  }

  withType<PublishTask> {
    token(prop("intellijPublishToken") ?: "unknown")
    channels(prop("intellijPublishChannels") ?: "")
  }

  patchPluginXml {
    fun fileInMetaInf(fileName: String) = file("src/main/resources/META-INF").resolve(fileName)

    fun String.replaceGitHubContentUrl(projectVersion: String): String = when {
      projectVersion.endsWith("-SNAPSHOT") -> "master"
      else -> projectVersion
    }
      .let { targetGitHubBranchName ->
        this.replace(
          "{{GIT_HUB_BRANCH}}",
          targetGitHubBranchName
        )
      }

    val version: String by project
    pluginDescription(fileInMetaInf("description.html").readText().replaceGitHubContentUrl(version))
    changeNotes(fileInMetaInf("changes.html").readText())
  }

  create("printVersion") {
    doLast {
      val version: String by project
      println(version)
    }
  }

  create(name = "generateTemplates", type = TemplateGeneratorTask::class) {
    resourceFiles = setOf("jcv", "jcv-db")
      .associateWith { file("$buildDir/resources/main/$it.xml") }
  }

  named("jar").configure {
    dependsOn("generateTemplates")
  }
}

fun prop(name: String): String? =
  extra.properties[name] as? String
