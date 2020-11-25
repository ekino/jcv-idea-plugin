import org.jetbrains.intellij.tasks.PublishTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.20"
  id("org.jetbrains.intellij") version "0.6.4"
}

intellij {
  version =
    "IC-2020.1" //IntelliJ IDEA 2020.1 dependency; for a full list of IntelliJ IDEA releases please see https://www.jetbrains.com/intellij-repository/releases
  pluginName = "JCV"
  updateSinceUntilBuild = false //Disables updating since-build attribute in plugin.xml
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.apache.commons:commons-text:1.9")

  testImplementation("com.willowtreeapps.assertk:assertk-jvm:${property("assertk-jvm.version")}")
  testImplementation("org.junit.jupiter:junit-jupiter:${property("junit.version")}")
  testImplementation("junit:junit:4.13")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine:${property("junit.version")}")
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    kotlinOptions.apiVersion = "1.3"
  }

  withType<Test> {
    useJUnitPlatform {
      includeEngines = setOf("junit-jupiter", "junit-vintage")
    }
    jvmArgs(
      "-Djunit.jupiter.testinstance.lifecycle.default=per_class",
      "-Duser.language=en"
    )
    testLogging {
      events("passed", "skipped", "failed")
    }
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
}

fun prop(name: String): String? =
  extra.properties[name] as? String
