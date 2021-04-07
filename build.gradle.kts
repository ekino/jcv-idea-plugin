import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.closure
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.21"
  // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
  id("org.jetbrains.intellij") version "0.6.5"
  // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
  id("org.jetbrains.changelog") version "0.6.2"
  // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
  id("io.gitlab.arturbosch.detekt") version "1.15.0"
  // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
  id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

// Import variables from gradle.properties file
val pluginGroup: String by project
// `pluginName_` variable ends with `_` because of the collision with Kotlin magic getter in the `intellij` closure.
// Read more about the issue: https://github.com/JetBrains/intellij-platform-plugin-template/issues/29
val pluginName_: String by project
val pluginDescription: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project
val pluginVerifierIdeVersions: String by project

val platformType: String by project
val platformVersion: String by project
val platformPlugins: String by project
val platformDownloadSources: String by project

group = pluginGroup
description = pluginDescription
version = pluginVersion

// Configure project's dependencies
repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${property("detekt-formatting.version")}")

  implementation("org.apache.commons:commons-text:${property("commons-text.version")}")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${property("jackson-module-kotlin.version")}")
  implementation(kotlin("reflect"))

  testImplementation("com.willowtreeapps.assertk:assertk-jvm:${property("assertk-jvm.version")}")
  testImplementation("org.junit.jupiter:junit-jupiter:${property("junit.version")}")
  testImplementation("junit:junit:4.13")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine:${property("junit.version")}")
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
  pluginName = pluginName_
  version = platformVersion
  type = platformType
  downloadSources = platformDownloadSources.toBoolean()
  updateSinceUntilBuild = true

  // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
  setPlugins(*platformPlugins.split(',').map(String::trim).filter(String::isNotEmpty).toTypedArray())
}

sourceSets["main"].java.srcDirs("src/main/gen")

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
  config = files("./detekt-config.yml")
  buildUponDefaultConfig = true

  reports {
    html.enabled = false
    xml.enabled = false
    txt.enabled = false
  }
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    kotlinOptions.apiVersion = "1.3"
  }

  withType<Detekt> {
    jvmTarget = JavaVersion.VERSION_11.toString()
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

  patchPluginXml {
    version(pluginVersion)
    sinceBuild(pluginSinceBuild)
    untilBuild(pluginUntilBuild)

    // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
    pluginDescription(
      closure {
        val gitHubContentBasePath = "https://raw.githubusercontent.com/ekino/jcv-idea-plugin"
        val gitHubRef = when {
          pluginVersion.endsWith("-SNAPSHOT") -> "master"
          else -> "v$pluginVersion"
        }

        File(projectDir, "README.md").readText().lines().run {
          val start = "<!-- Plugin description -->"
          val end = "<!-- Plugin description end -->"

          if (!containsAll(listOf(start, end))) {
            throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
          }
          subList(indexOf(start) + 1, indexOf(end))
        }
          .joinToString("\n")
          .run { markdownToHTML(this) }
          // Replace local url with GitHub base url and set width to 500px
          .replace(
            """src="./""",
            """width="500" src="$gitHubContentBasePath/$gitHubRef/"""
          )
      }
    )

    // Get the latest available change notes from the changelog file
    changeNotes(
      closure {
        changelog.getLatest().toHTML()
      }
    )
  }

  runPluginVerifier {
    ideVersions(pluginVerifierIdeVersions)
  }

  publishPlugin {
    dependsOn("patchChangelog")
    token(System.getenv("PUBLISH_TOKEN"))
    // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
    // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
    // https://jetbrains.org/intellij/sdk/docs/tutorials/build_system/deployment.html#specifying-a-release-channel
    channels(pluginVersion.split('-').getOrElse(1) { "default" }.split('.').first())
  }
}
