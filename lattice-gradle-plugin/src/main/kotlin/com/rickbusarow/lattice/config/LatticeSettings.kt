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

import com.rickbusarow.kgx.extras
import com.rickbusarow.kgx.getOrPut
import com.rickbusarow.lattice.config.LatticeSettings.JavaSettingsGroup
import com.rickbusarow.lattice.generator.LatticeSettingsSchema
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import java.io.Serializable as JavaSerializable

public val Project.latticeSettings: LatticeSettings
  get() = extras.getOrPut("latticeSettings") {
    objects.newInstance(com.rickbusarow.lattice.config.internal.DefaultLatticeSettings::class.java)
  }

@LatticeSettingsSchema
public interface LatticeSettings : JavaSerializable {

  public val versions: VersionsGroup

  public interface VersionsGroup : JavaSerializable {
    /**
     * The Dokka version to be used by the Dokkatoo plugin.
     *
     * ```properties
     * lattice.versions.dokka=1.9.20
     * ```
     */
    public val dokka: Provider<String>
  }

  public val kotlin: KotlinSettingsGroup

  public interface KotlinSettingsGroup : JavaSerializable {
    public val apiLevel: Provider<String>
    public val allWarningsAsErrors: Provider<Boolean>
    public val explicitApi: Provider<Boolean>
  }

  public val java: JavaSettingsGroup

  public interface JavaSettingsGroup : JavaSerializable {
    public val jvmTarget: Provider<String>
    public val jvmSource: Provider<String>
    public val jvmToolchain: Provider<String>
  }

  public val repository: RepositorySettingsGroup

  public interface RepositorySettingsGroup : JavaSerializable {

    public val defaultBranch: Provider<String>

    public val github: GithubSettingsGroup

    public interface GithubSettingsGroup : JavaSerializable {
      public val owner: Provider<String>
      public val repo: Provider<String>
      public val url: Provider<String>
    }
  }

  public val publishing: PublishingSettingsGroup

  public interface PublishingSettingsGroup : JavaSerializable {

    public val pom: PomSettingsGroup

    public interface PomSettingsGroup : JavaSerializable {
      public val artifactId: Provider<String>
      public val name: Provider<String>
      public val description: Provider<String>
      public val inceptionYear: Provider<String>
      public val url: Provider<String>

      public val license: LicenseSettingsGroup

      public interface LicenseSettingsGroup : JavaSerializable {
        public val name: Provider<String>
        public val url: Provider<String>
        public val dist: Provider<String>
      }

      public val scm: ScmSettingsGroup

      public interface ScmSettingsGroup : JavaSerializable {
        public val url: Provider<String>
        public val connection: Provider<String>
        public val devConnection: Provider<String>
      }

      public val developer: DeveloperSettingsGroup

      public interface DeveloperSettingsGroup : JavaSerializable {
        public val id: Provider<String>
        public val name: Provider<String>
        public val url: Provider<String>
      }
    }
  }
}

public fun JavaSettingsGroup.jvmTargetInt(): Provider<Int> =
  jvmTarget.map { it.substringAfterLast('.').toInt() }

public fun JavaSettingsGroup.jvmSourceInt(): Provider<Int> =
  jvmSource.map { it.substringAfterLast('.').toInt() }

public fun JavaSettingsGroup.jvmToolchainInt(): Provider<Int> =
  jvmToolchain.map { it.substringAfterLast('.').toInt() }
