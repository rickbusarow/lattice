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

package com.rickbusarow.lattice.core

import com.rickbusarow.kgx.extras
import com.rickbusarow.kgx.getOrPut
import com.rickbusarow.kgx.gradleLazy
import dev.drewhamilton.poko.Poko
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.jetbrains.kotlin.gradle.plugin.extraProperties
import javax.inject.Inject

@Suppress("MemberNameEqualsClassName")
internal val Project.latticeSettings: LatticeSettings
  get() = extras.getOrPut("latticeSettings") {
    objects.newInstance(LatticeSettings::class.java)
  }

@Poko
public open class LatticeSettings @Inject constructor(
  private val providers: ProviderFactory,
  private val target: Project
) : java.io.Serializable {

  public val GROUP: Provider<String> = providers.gradleProperty("GROUP")
  public val VERSION_NAME: Provider<String>
    get() = target.provider { target.extraProperties.get("VERSION_NAME") as String }
  // get() = providers.gradleProperty("VERSION_NAME")

  public val versions: Versions by gradleLazy { Versions() }

  public inner class Versions {
    public val dokka: Provider<String> = providers.gradleProperty("lattice.versions.dokka")
  }

  public val kotlin: KotlinSettings by gradleLazy { KotlinSettings() }

  public inner class KotlinSettings {
    public val apiLevel: Provider<String> = providers.gradleProperty("lattice.kotlin.apiLevel")
    public val allWarningsAsErrors: Provider<Boolean> = providers
      .gradleProperty("lattice.kotlin.allWarningsAsErrors").map { it.toBoolean() }
    public val explicitApi: Provider<Boolean> = providers
      .gradleProperty("lattice.kotlin.explicitApi").map { it.toBoolean() }
  }

  public val java: JavaSettings by gradleLazy { JavaSettings() }

  public inner class JavaSettings {
    public val JVM_TARGET: Provider<String> = providers.gradleProperty("JVM_TARGET")
    public val JVM_TARGET_INT: Provider<Int> = JVM_TARGET.map { it.substringAfterLast('.').toInt() }
    public val JVM_SOURCE: Provider<String> = providers.gradleProperty("JVM_SOURCE")
    public val JVM_SOURCE_INT: Provider<Int> = JVM_SOURCE.map { it.substringAfterLast('.').toInt() }
    public val JVM_TOOLCHAIN: Provider<String> = providers.gradleProperty("JVM_TOOLCHAIN")
    public val JVM_TOOLCHAIN_INT: Provider<Int> = JVM_TOOLCHAIN.map {
      it.substringAfterLast('.').toInt()
    }
  }

  public val repository: RepositorySettings by gradleLazy { RepositorySettings() }

  public inner class RepositorySettings {
    public val defaultBranch: Provider<String> = providers.gradleProperty(
      "lattice.repository.defaultBranch"
    )
    public val github: GithubSettings by gradleLazy { GithubSettings() }

    public inner class GithubSettings {
      public val owner: Provider<String> = providers.gradleProperty("GITHUB_OWNER")
      public val repo: Provider<String> = providers.gradleProperty("GITHUB_REPO")
      public val url: Provider<String> = owner.zip(repo) { owner, repo ->
        "https://github.com/$owner/$repo"
      }
    }
  }

  public val publishing: PublishingSettings by gradleLazy { PublishingSettings() }

  public inner class PublishingSettings {

    public val pom: PomSettings by gradleLazy { PomSettings() }

    public inner class PomSettings {

      public val artifactId: Provider<String> = providers.gradleProperty("POM_ARTIFACT_ID")
      public val name: Provider<String> = providers.gradleProperty("POM_NAME")
      public val description: Provider<String> = providers.gradleProperty("POM_DESCRIPTION")
      public val inceptionYear: Provider<String> = providers.gradleProperty("POM_INCEPTION_YEAR")
      public val url: Provider<String> = providers.gradleProperty("POM_URL")

      public val license: LicenseSettings by gradleLazy { LicenseSettings() }

      public inner class LicenseSettings {
        public val name: Provider<String> = providers.gradleProperty("POM_LICENSE_NAME")
        public val url: Provider<String> = providers.gradleProperty("POM_LICENSE_URL")
        public val dist: Provider<String> = providers.gradleProperty("POM_LICENSE_DIST")
      }

      public val scm: ScmSettings by gradleLazy { ScmSettings() }

      public inner class ScmSettings {
        public val url: Provider<String> = providers.gradleProperty("POM_SCM_URL")
        public val connection: Provider<String> = providers.gradleProperty("POM_SCM_CONNECTION")
        public val devConnection: Provider<String> =
          providers.gradleProperty("POM_SCM_DEV_CONNECTION")
      }

      public val developer: DeveloperSettings by gradleLazy { DeveloperSettings() }

      public inner class DeveloperSettings {
        public val id: Provider<String> = providers.gradleProperty("POM_DEVELOPER_ID")
        public val name: Provider<String> = providers.gradleProperty("POM_DEVELOPER_NAME")
        public val url: Provider<String> = providers.gradleProperty("POM_DEVELOPER_URL")
      }
    }
  }
}
