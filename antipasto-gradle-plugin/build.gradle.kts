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
import com.rickbusarow.kgx.getOrPut
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

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
val GITHUB_REPOSITORY: String by project

fun PluginDeclaration.tags(vararg v: String) {
  @Suppress("UnstableApiUsage")
  tags.set(v.toList())
}

gradlePlugin {

  @Suppress("UnstableApiUsage")
  vcsUrl.set(GITHUB_REPOSITORY)
  @Suppress("UnstableApiUsage")
  website.set(GITHUB_REPOSITORY)

  plugins {

    create("root") {
      id = "com.rickbusarow.antipasto.root"
      implementationClass = "com.rickbusarow.antipasto.RootPlugin"
      description = "Convention plugin for the root project of a multi-module build"
      tags("convention-plugin", "kotlin", "java", "jvm", "kotlin-jvm")
    }
    create("composite") {
      id = "com.rickbusarow.antipasto.composite"
      implementationClass = "com.rickbusarow.antipasto.CompositePlugin"
      description = "Convention plugin for making composite Gradle builds easier"
      tags("convention-plugin", "kotlin", "java", "jvm", "kotlin-jvm")
    }
    create("java-gradle-plugin") {
      id = "com.rickbusarow.antipasto.java-gradle-plugin"
      implementationClass = "com.rickbusarow.antipasto.KotlinJvmModulePlugin"
      description = "Convention plugin for a java-gradle-plugin project"
      tags("convention-plugin", "kotlin", "plugin", "java", "jvm", "kotlin-jvm")
    }
    create("jvm") {
      id = "com.rickbusarow.antipasto.kotlin-jvm"
      implementationClass = "com.rickbusarow.antipasto.GradlePluginModulePlugin"
      description = "Convention plugin for a Kotlin JVM project"
      tags("convention-plugin", "kotlin", "java", "jvm", "kotlin-jvm")
    }
    create("kmp") {
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

  extensions.configure<MavenPublishBaseExtension> {
    publishToMavenCentral(SonatypeHost.DEFAULT, automaticRelease = true)
    signAllPublications()
    pom {
      name.set("Antipasto")
      url.set(GITHUB_REPOSITORY)

      licenses {
        license {
          name.set("The Apache License, Version 2.0")
          url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
          distribution.set("repo")
        }
      }
      scm {
        val scm = this
        scm.url.set(GITHUB_REPOSITORY)
        scm.connection.set("scm:git:git://github.com/$GITHUB_OWNER_REPO.git")
        scm.developerConnection.set("scm:git:ssh://git@github.com/$GITHUB_OWNER_REPO.git")
      }
      developers {
        developer {
          id.set(GITHUB_OWNER)
          name.set(DEVELOPER_NAME)
          url.set(DEVELOPER_URL)
        }
      }
    }
  }

  fun MavenPublication.isPluginMarker(): Boolean = name.endsWith("PluginMarkerMaven")
  fun Publication.isPluginMarker(): Boolean = (this as? MavenPublication)?.isPluginMarker() ?: false

  publishing {
    publications.withType<MavenPublication>().configureEach pub@{
      val publication = this@pub

      publication.groupId = project.group as String

      if (publication.isPluginMarker()) {
        val plugin = gradlePlugin.plugins[publication.name.removeSuffix("PluginMarkerMaven")]
        publication.pom.description.set(plugin.description)
      } else {
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

  val skipDokka = extras.getOrPut("skipDokka") { false }

  tasks.withType(Jar::class.java).configureEach {

    val task = this

    if (task.name == "javadocJar" && !skipDokka) {
      val dokka = tasks.named("dokkatooGeneratePublicationHtml")
      task.archiveClassifier.set("javadoc")
      task.dependsOn(dokka)
      task.from(dokka)
    }
  }

  tasks.withType(GenerateModuleMetadata::class.java).configureEach {
    mustRunAfter("javadocJar")
  }
  tasks.withType(AbstractPublishToMaven::class.java).configureEach {
    mustRunAfter(tasks.withType(Jar::class.java))
  }
  tasks.withType(Sign::class.java).configureEach {
    mustRunAfter(tasks.withType(Jar::class.java))
  }

  tasks.register("publishToBuildM2") {
    dependsOn("publishAllPublicationsToBuildM2Repository")
  }
}
