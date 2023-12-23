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

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `java-gradle-plugin`
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.poko)
  idea
}

if (rootProject.name == "lattice") {
  apply(plugin = "com.rickbusarow.lattice.jvm-module")
}

gradlePlugin {
  plugins {
    create("composite") {
      id = "com.rickbusarow.lattice.composite"
      implementationClass = "com.rickbusarow.lattice.CompositePlugin"
    }
    create("jvm") {
      id = "com.rickbusarow.lattice.jvm-module"
      implementationClass = "com.rickbusarow.lattice.KotlinJvmModulePlugin"
    }
    create("kmp") {
      id = "com.rickbusarow.lattice.kmp-module"
      implementationClass = "com.rickbusarow.lattice.KotlinMultiplatformModulePlugin"
    }
    create("root") {
      id = "com.rickbusarow.lattice.root"
      implementationClass = "com.rickbusarow.lattice.RootPlugin"
    }
    create("curator") {
      id = "com.rickbusarow.lattice.curator"
      implementationClass = "com.rickbusarow.lattice.curator.CuratorPlugin"
    }

    fun convention(suffix: String, simpleClassName: String) {
      create("convention.$suffix") {
        id = "com.rickbusarow.lattice.$suffix"
        implementationClass = "com.rickbusarow.lattice.conventions.$simpleClassName"
      }
    }

    convention("ben-manes", "BenManesVersionsPlugin")
    convention("check", "CheckPlugin")
    convention("clean", "CleanPlugin")
    convention("dependency-guard", "DependencyGuardConventionPlugin")
    convention("detekt", "DetektConventionPlugin")
    convention("dokkatoo", "DokkatooConventionPlugin")
    convention("dokka-versioning", "DokkaVersionArchivePlugin")
    convention("github-release", "GitHubReleasePlugin")
    convention("gradle-tests", "GradleTestsPlugin")
    convention("kotlin-jvm", "KotlinJvmConventionPlugin")
    convention("ktlint", "KtLintConventionPlugin")
    convention("spotless", "SpotlessConventionPlugin")
    convention("test", "TestConventionPlugin")
  }
}

val gradleTest by sourceSets.registering {
  val ss = this@registering

  val main by sourceSets.getting

  gradlePlugin.testSourceSets(ss)

  ss.compileClasspath += main.output
  ss.runtimeClasspath += main.output

  configurations.named(ss.implementationConfigurationName) {
    extendsFrom(configurations.getByName(main.implementationConfigurationName))
  }
  configurations.named(ss.runtimeOnlyConfigurationName) {
    extendsFrom(configurations.getByName(main.runtimeOnlyConfigurationName))
  }
  configurations.named(ss.compileOnlyConfigurationName) {
    extendsFrom(configurations.getByName(main.compileOnlyConfigurationName))
  }
}

tasks.register("gradleTest", Test::class) {
  useJUnitPlatform()

  val javaSourceSet = gradleTest.get()

  testClassesDirs = javaSourceSet.output.classesDirs
  classpath = javaSourceSet.runtimeClasspath
  inputs.files(javaSourceSet.allSource)
}

tasks.named("check") { dependsOn("gradleTest") }

idea {
  module {
    testSources.from(gradleTest.map { it.allSource.srcDirs })
  }
}

val gradleTestImplementation: Configuration by configurations.getting

dependencies {

  compileOnly(gradleApi())

  compileOnly(project(":lattice-settings-annotations"))

  gradleTestImplementation(libs.junit.engine)
  gradleTestImplementation(libs.junit.jupiter)
  gradleTestImplementation(libs.junit.jupiter.api)
  gradleTestImplementation(libs.junit.params)
  gradleTestImplementation(libs.kase)
  gradleTestImplementation(libs.kase.gradle)
  gradleTestImplementation(libs.kase.gradle.dsl)
  gradleTestImplementation(libs.kotest.assertions.api)
  gradleTestImplementation(libs.kotest.assertions.core.jvm)
  gradleTestImplementation(libs.kotest.assertions.shared)

  implementation(libs.benManes.versions)
  implementation(libs.detekt.gradle)
  implementation(libs.diffplug.spotless)
  implementation(libs.dokka.core)
  implementation(libs.dokka.gradle)
  implementation(libs.dokka.versioning)
  implementation(libs.dokkatoo.plugin)
  implementation(libs.dropbox.dependencyGuard)
  implementation(libs.ec4j.core)
  implementation(libs.johnrengelman.shadowJar)
  implementation(libs.kotlin.gradle.plugin)
  implementation(libs.kotlin.gradle.plugin.api)
  implementation(libs.kotlin.reflect)
  implementation(libs.kotlinx.binaryCompatibility)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.picnic)
  implementation(libs.poko.gradle.plugin)
  implementation(libs.rickBusarow.doks)
  implementation(libs.rickBusarow.github.release)
  implementation(libs.rickBusarow.kgx)
  implementation(libs.rickBusarow.ktlint)
  implementation(libs.rickBusarow.moduleCheck.gradle.plugin) {
    exclude(group = "org.jetbrains.kotlin")
  }
  implementation(libs.vanniktech.publish)

  ksp(project(":lattice-settings-generator"))

  testImplementation(libs.junit.engine)
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.junit.jupiter.api)
  testImplementation(libs.junit.params)
  testImplementation(libs.kase)
  testImplementation(libs.kotest.assertions.api)
  testImplementation(libs.kotest.assertions.core.jvm)
  testImplementation(libs.kotest.assertions.shared)
}

tasks.named("compileKotlin", KotlinCompile::class) {
  kotlinOptions {
    explicitApiMode.set(Strict)
  }
}
