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
import com.rickbusarow.lattice.core.SettingsElement.SettingsGroup
import dev.drewhamilton.poko.Poko
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.jetbrains.kotlin.gradle.plugin.extraProperties
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.properties.ReadOnlyProperty

@Suppress("MemberNameEqualsClassName")
internal val Project.latticeSettings: LatticeSettings
  get() = extras.getOrPut("latticeSettings") {
    objects.newInstance(LatticeSettings::class.java)
  }

public sealed interface SettingsElement : Serializable, Comparable<SettingsElement> {
  public val name: String
  public val parent: SettingsElement?

  override fun compareTo(other: SettingsElement): Int = name.compareTo(other.name)

  public abstract class SettingsGroup : SettingsElement {
    @Suppress("PropertyName")
    @PublishedApi
    internal val _children: ConcurrentHashMap<String, SettingsElement> =
      ConcurrentHashMap<String, SettingsElement>()
    public val children: List<SettingsElement>
      get() = _children.values.sorted()

    public inline fun <reified T : SettingsGroup> group(): ReadOnlyProperty<Any, T> {
      return ReadOnlyProperty { _, property ->
        _children.computeIfAbsent(property.name) {
          T::class.constructors.single().call(property.name, this@SettingsGroup)
        } as T
      }
    }

    public inline fun <reified T> value(): ReadOnlyProperty<Any, Provider<T>> {
      return ReadOnlyProperty { _, property ->

        val element = _children.computeIfAbsent(property.name) {
          SettingsValue<T>(property.name, this@SettingsGroup)
        }

        @Suppress("UNCHECKED_CAST")
        (element as SettingsValue<T>).value
      }
    }
  }

  @Poko
  public class SettingsValue<T>(
    override val name: String,
    override val parent: SettingsElement?
  ) : SettingsElement {
    public val value: Provider<T> = TODO()
  }
}

@Poko
public class VersionsGroup(
  override val name: String,
  override val parent: SettingsElement?
) : SettingsGroup()

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

  override fun toString(): String {
    return "LatticeSettings(\n" +
      "  GROUP=${GROUP.orNull}\n" +
      "  VERSION_NAME=${VERSION_NAME.orNull}\n" +
      "  versions.dokka=${versions.dokka.orNull}\n" +
      "  kotlin.apiLevel=${kotlin.apiLevel.orNull}\n" +
      "  kotlin.allWarningsAsErrors=${kotlin.allWarningsAsErrors.orNull}\n" +
      "  kotlin.explicitApi=${kotlin.explicitApi.orNull}\n" +
      "  java.JVM_TARGET=${java.JVM_TARGET.orNull}\n" +
      "  java.JVM_TARGET_INT=${java.JVM_TARGET_INT.orNull}\n" +
      "  java.JVM_SOURCE=${java.JVM_SOURCE.orNull}\n" +
      "  java.JVM_SOURCE_INT=${java.JVM_SOURCE_INT.orNull}\n" +
      "  java.JVM_TOOLCHAIN=${java.JVM_TOOLCHAIN.orNull}\n" +
      "  java.JVM_TOOLCHAIN_INT=${java.JVM_TOOLCHAIN_INT.orNull}\n" +
      "  repository.defaultBranch=${repository.defaultBranch.orNull}\n" +
      "  repository.github.owner=${repository.github.owner.orNull}\n" +
      "  repository.github.repo=${repository.github.repo.orNull}\n" +
      "  repository.github.url=${repository.github.url.orNull}\n" +
      "  publishing.pom.artifactId=${publishing.pom.artifactId.orNull}\n" +
      "  publishing.pom.name=${publishing.pom.name.orNull}\n" +
      "  publishing.pom.description=${publishing.pom.description.orNull}\n" +
      "  publishing.pom.inceptionYear=${publishing.pom.inceptionYear.orNull}\n" +
      "  publishing.pom.url=${publishing.pom.url.orNull}\n" +
      "  publishing.pom.license.name=${publishing.pom.license.name.orNull}\n" +
      "  publishing.pom.license.url=${publishing.pom.license.url.orNull}\n" +
      "  publishing.pom.license.dist=${publishing.pom.license.dist.orNull}\n" +
      "  publishing.pom.scm.url=${publishing.pom.scm.url.orNull}\n" +
      "  publishing.pom.scm.connection=${publishing.pom.scm.connection.orNull}\n" +
      "  publishing.pom.scm.devConnection=${publishing.pom.scm.devConnection.orNull}\n" +
      "  publishing.pom.developer.id=${publishing.pom.developer.id.orNull}\n" +
      "  publishing.pom.developer.name=${publishing.pom.developer.name.orNull}\n" +
      "  publishing.pom.developer.url=${publishing.pom.developer.url.orNull}\n" +
      ")"
  }
}
