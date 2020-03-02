import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.70"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.fasterxml.jackson.core:jackson-databind:${property("jackson.version")}")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${property("jackson.version")}")

  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${property("jackson.version")}")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${property("jackson.version")}")

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
}
