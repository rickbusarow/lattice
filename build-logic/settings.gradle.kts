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

rootProject.name = "build-logic"

pluginManagement {
  val allowMavenLocal = providers
    .gradleProperty("${rootProject.name}.allow-maven-local")
    .orNull.toBoolean()

  repositories {
    if (allowMavenLocal) {
      logger.lifecycle("${rootProject.name} -- allowing mavenLocal for plugins")
      mavenLocal()
    }
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

val allowMavenLocal = providers
  .gradleProperty("${rootProject.name}.allow-maven-local")
  .orNull.toBoolean()

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    if (allowMavenLocal) {
      logger.lifecycle("${rootProject.name} -- allowing mavenLocal for dependencies")
      mavenLocal()
    }
    gradlePluginPortal()
    google()
    mavenCentral()
  }
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}

listOf(
  "lattice-gradle-plugin",
  "lattice-settings-annotations",
  "lattice-settings-generator"
).forEach { name ->
  include(":$name")
  project(":$name").projectDir = file("../$name")
}
