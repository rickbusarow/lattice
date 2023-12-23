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

package com.rickbusarow.lattice.config

import com.rickbusarow.kase.gradle.GradleTestVersions
import com.rickbusarow.lattice.LatticeGradleTest
import org.junit.jupiter.api.TestFactory
import java.io.File

class LatticeSettingsTest : LatticeGradleTest<GradleTestVersions> {

  override val kases: List<GradleTestVersions>
    get() = versionMatrix.versions(GradleTestVersions).takeLast(1)

  @TestFactory
  fun `canary thing`() = testFactory {

    rootProject {

      buildFile(
        """
        import com.rickbusarow.lattice.config.latticeSettings

        plugins {
          id("com.rickbusarow.lattice.root")
        }

        val ls = latticeSettings

        val foo by tasks.registering {
          doLast {
            println(ls)
          }
        }
        """.trimIndent()
      )

      file(
        "gradle/libs.versions.toml",
        File("../gradle/libs.versions.toml").readText()
      )

      gradlePropertiesFile(
        """
        VERSION_NAME=0.1.0-SNAPSHOT

        GROUP=com.rickbusarow.lattice

        GITHUB_OWNER=rickbusarow
        DEVELOPER_NAME=Rick Busarow
        DEVELOPER_URL=https://github.com/rbusarow
        GITHUB_OWNER_REPO=rbusarow/lattice
        GITHUB_REPOSITORY=https://github.com/rbusarow/lattice

        KOTLIN_API=1.7

        JDK_BUILD_LOGIC=17
        JVM_TARGET_BUILD_LOGIC=11

        JDK=17
        JVM_TARGET=11

        """.trimIndent()
      )
    }

    shouldSucceed("foo", withPluginClasspath = true)
  }
}
