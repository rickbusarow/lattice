/*
 * Copyright (C) 2023 Rick Busarow
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("VariableNaming")

import com.rickbusarow.kgx.extras

buildscript {
  dependencies {
    classpath(libs.rickBusarow.kgx)
  }
}

plugins {
  `java-gradle-plugin`
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.poko)
  alias(libs.plugins.plugin.publish)
  alias(libs.plugins.vanniktech.publish) apply false
}

dependencies {

  api(libs.breadmoirai.github.release)
  api(libs.integration.test) {
    exclude(group = "org.jetbrains.kotlin")
  }
  api(libs.rickBusarow.doks)
  api(libs.rickBusarow.kgx)
  api(libs.rickBusarow.ktlint)

  compileOnly(gradleApi())

  implementation(libs.benManes.versions)
  implementation(libs.detekt.gradle)
  implementation(libs.diffplug.spotless)
  implementation(libs.dokka.core)
  implementation(libs.dokka.gradle)
  implementation(libs.dokka.versioning)
  implementation(libs.dokkatoo.plugin)
  implementation(libs.dropbox.dependencyGuard)
  implementation(libs.johnrengelman.shadowJar)
  implementation(libs.kotlin.gradle.plugin)
  implementation(libs.kotlin.gradle.plugin.api)
  implementation(libs.kotlin.reflect)
  implementation(libs.kotlinx.binaryCompatibility)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.picnic)
  implementation(libs.poko.gradle.plugin)
  implementation(libs.rickBusarow.moduleCheck.gradle.plugin) {
    exclude(group = "org.jetbrains.kotlin")
  }
  implementation(libs.vanniktech.publish)
}

val GITHUB_OWNER: String by project
val DEVELOPER_URL: String by project
val DEVELOPER_NAME: String by project
val GITHUB_OWNER_REPO: String by project

fun PluginDeclaration.tags(vararg v: String) {
  @Suppress("UnstableApiUsage")
  tags.set(v.toList())
}

gradlePlugin {

  plugins {

    register("root") {
      id = "com.rickbusarow.antipasto.root"
      implementationClass = "com.rickbusarow.antipasto.RootPlugin"
      description = "Convention plugin for the root project of a multi-module build"
      tags("convention-plugin", "kotlin", "java", "jvm", "kotlin-jvm")
    }
    register("composite") {
      id = "com.rickbusarow.antipasto.composite"
      implementationClass = "com.rickbusarow.antipasto.composite.CompositePlugin"
      description = "Convention plugin for making composite Gradle builds easier"
      tags("convention-plugin", "kotlin", "java", "jvm", "kotlin-jvm")
    }
    register("java-gradle-plugin") {
      id = "com.rickbusarow.antipasto.java-gradle-plugin"
      implementationClass = "com.rickbusarow.antipasto.GradlePluginModulePlugin"
      description = "Convention plugin for a java-gradle-plugin project"
      tags("convention-plugin", "kotlin", "plugin", "java", "jvm", "kotlin-jvm")
    }
    register("jvm") {
      id = "com.rickbusarow.antipasto.kotlin-jvm"
      implementationClass = "com.rickbusarow.antipasto.KotlinJvmModulePlugin"
      description = "Convention plugin for a Kotlin JVM project"
      tags("convention-plugin", "kotlin", "java", "jvm", "kotlin-jvm")
    }
    register("kmp") {
      id = "com.rickbusarow.antipasto.kotlin-multiplatform"
      implementationClass = "com.rickbusarow.antipasto.KotlinMultiplatformModulePlugin"
      description = "Convention plugin for a Kotlin Multiplatform project"
      tags("convention-plugin", "kotlin", "multiplatform", "kotlin-multiplatform")
    }
  }
}
if (rootProject.name == "antipasto") {
  apply(plugin = "com.rickbusarow.antipasto.java-gradle-plugin")

  apply(plugin = libs.plugins.vanniktech.publish.get().pluginId)

  gradlePlugin {

    val GITHUB_REPOSITORY: String by project
    @Suppress("UnstableApiUsage")
    vcsUrl.set(GITHUB_REPOSITORY)
    @Suppress("UnstableApiUsage")
    website.set(GITHUB_REPOSITORY)
  }

  fun MavenPublication.isPluginMarker(): Boolean = name.endsWith("PluginMarkerMaven")
  fun Publication.isPluginMarker(): Boolean = (this as? MavenPublication)?.isPluginMarker() ?: false

  publishing {
    publications.withType<MavenPublication>().configureEach pub@{
      val publication = this@pub

      publication.groupId = project.group as String

      if (!publication.isPluginMarker()) {
        publication.artifactId = "antipasto-gradle-plugin"
        publication.pom.description.set("Convention plugins for Gradle builds")
      }
    }

    repositories {
      maven {
        name = "buildM2"
        setUrl(layout.buildDirectory.dir("m2"))
      }
    }
  }

  tasks.register("publishToBuildM2") {
    dependsOn("publishAllPublicationsToBuildM2Repository")
  }
  tasks.register("publishToBuildM2NoDokka") {
    project.extras.set("skipDokka", true)
    dependsOn("publishAllPublicationsToBuildM2Repository")
  }
}
