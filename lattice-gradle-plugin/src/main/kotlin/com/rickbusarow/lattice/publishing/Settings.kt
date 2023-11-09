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

package com.rickbusarow.lattice.publishing

import dev.drewhamilton.poko.Poko
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.jetbrains.kotlin.gradle.utils.property
import javax.inject.Inject

@Poko
public open class Settings @Inject constructor(
  private val providers: ProviderFactory
) {

  public val GROUP: Provider<String> = providers.gradleProperty("GROUP")
  public val VERSION_NAME: Provider<String> = providers.gradleProperty("VERSION_NAME")

  public val java: Java by property { Java() }

  public inner class Java {
    public val KOTLIN_API: Provider<String> = providers.gradleProperty("KOTLIN_API")
    public val JVM_TARGET: Provider<String> = providers.gradleProperty("JVM_TARGET")
    public val JVM_SOURCE: Provider<String> = providers.gradleProperty("JVM_SOURCE")
    public val JVM_TOOLCHAIN: Provider<String> = providers.gradleProperty("JVM_TOOLCHAIN")
  }

  public val publishing: Publishing by property { Publishing() }

  public inner class Publishing {

    public val POM_ARTIFACT_ID: Provider<String> = providers.gradleProperty("POM_ARTIFACT_ID")
    public val POM_NAME: Provider<String> = providers.gradleProperty("POM_NAME")
    public val POM_DESCRIPTION: Provider<String> = providers.gradleProperty("POM_DESCRIPTION")
    public val POM_INCEPTION_YEAR: Provider<String> = providers.gradleProperty("POM_INCEPTION_YEAR")
    public val POM_URL: Provider<String> = providers.gradleProperty("POM_URL")
    public val POM_LICENSE_NAME: Provider<String> = providers.gradleProperty("POM_LICENSE_NAME")
    public val POM_LICENSE_URL: Provider<String> = providers.gradleProperty("POM_LICENSE_URL")
    public val POM_LICENSE_DIST: Provider<String> = providers.gradleProperty("POM_LICENSE_DIST")
    public val POM_SCM_URL: Provider<String> = providers.gradleProperty("POM_SCM_URL")
    public val POM_SCM_CONNECTION: Provider<String> = providers.gradleProperty("POM_SCM_CONNECTION")
    public val POM_SCM_DEV_CONNECTION: Provider<String> =
      providers.gradleProperty("POM_SCM_DEV_CONNECTION")
    public val POM_DEVELOPER_ID: Provider<String> = providers.gradleProperty("POM_DEVELOPER_ID")
    public val POM_DEVELOPER_NAME: Provider<String> = providers.gradleProperty("POM_DEVELOPER_NAME")
    public val POM_DEVELOPER_URL: Provider<String> = providers.gradleProperty("POM_DEVELOPER_URL")
  }
}
