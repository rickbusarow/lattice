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

import org.gradle.plugins.ide.idea.model.IdeaModel

buildscript {
  dependencies {
    classpath(libs.rickBusarow.kgx)
  }
}

plugins {
  alias(libs.plugins.poko) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.ktlint) apply false
  alias(libs.plugins.doks)
  alias(libs.plugins.vanniktech.publish) apply false
  alias(libs.plugins.moduleCheck)
  id("com.rickbusarow.lattice.kotlin-jvm") apply false
  id("com.rickbusarow.lattice.root")
}

moduleCheck {
  deleteUnused = true
  checks.sortDependencies = true
}

lattice {
  composite {
  }
  github {
  }
  dokka {
  }
  java {
  }
}

if (gradle.includedBuilds.any { it.name == "build-logic" }) {
  subprojects sub@{

    val sub = this@sub

    sub.plugins.withId("build-init") {

      val ktlintPluginId = libs.plugins.ktlint.get().pluginId

      sub.apply(plugin = ktlintPluginId)

      dependencies {
        "ktlint"(libs.rickBusarow.ktrules)
      }
    }

    sub.layout.buildDirectory.set(sub.file("build/build-main"))

    sub.apply(plugin = "idea")

    sub.extensions.configure(IdeaModel::class) {
      module {
        generatedSourceDirs.add(sub.file("build"))
        excludeDirs.add(sub.file("build"))
      }
    }
  }
}
