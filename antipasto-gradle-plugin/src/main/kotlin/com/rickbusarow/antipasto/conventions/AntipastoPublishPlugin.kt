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

package com.rickbusarow.antipasto.conventions

import com.rickbusarow.kgx.extras
import com.rickbusarow.kgx.getOrPut
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.Publication
import org.gradle.api.publish.maven.MavenPublication

/**
 *
 */
public abstract class AntipastoPublishPlugin : Plugin<Project> {

  override fun apply(target: Project) {

    val skipDokka = target.extras.getOrPut("skipDokka") { false }

    val GITHUB_OWNER = target.extras.get("GITHUB_OWNER") as String
    val DEVELOPER_URL = target.extras.get("DEVELOPER_URL") as String
    val DEVELOPER_NAME = target.extras.get("DEVELOPER_NAME") as String
    val GITHUB_OWNER_REPO = target.extras.get("GITHUB_OWNER_REPO") as String
    val GITHUB_REPOSITORY = target.extras.get("GITHUB_REPOSITORY") as String

    val artifactId = "This is not a real artifactId"

    target.plugins.apply("com.vanniktech.maven.publish.base")

    val maven = target.mavenPublishBaseExtension

    maven.publishToMavenCentral(SonatypeHost.DEFAULT, automaticRelease = true)
    maven.signAllPublications()

    maven.pom { mavenPom ->
      mavenPom.description.set("~~~~~~~~~~~~~~~~~~~~~~~~~")
      mavenPom.name.set(artifactId)

      mavenPom.url.set("https://www.github.com/$GITHUB_OWNER_REPO/")

      mavenPom.licenses { licenseSpec ->
        licenseSpec.license { license ->
          license.name.set("The Apache Software License, Version 2.0")
          license.url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
          license.distribution.set("repo")
        }
      }
      mavenPom.scm { scm ->
        scm.url.set("https://www.github.com/$GITHUB_OWNER_REPO/")
        scm.connection.set("scm:git:git://github.com/$GITHUB_OWNER_REPO.git")
        scm.developerConnection.set("scm:git:ssh://git@github.com/$GITHUB_OWNER_REPO.git")
      }
      mavenPom.developers { developerSpec ->
        developerSpec.developer { developer ->
          developer.id.set(GITHUB_OWNER)
          developer.name.set(target.property("DEVELOPER_NAME") as String)
          developer.url.set(target.property("DEVELOPER_URL") as String)
        }
      }
    }

    val gradlePublish = target.gradlePublishingExtension

    gradlePublish.publications.withType(MavenPublication::class.java).configureEach { publication ->
      publication.groupId = target.group as String

      if (publication.isPluginMarker()) {
        val plugin = gradlePublish
        publication.pom.description.set(plugin.description)
      } else {
        publication.artifactId = "antipasto-gradle-plugin"
        publication.pom.description.set("Convention plugins for Gradle builds")
      }
    }
  }

  private fun MavenPublication.isPluginMarker(): Boolean = name.endsWith("PluginMarkerMaven")
  private fun Publication.isPluginMarker(): Boolean =
    (this as? MavenPublication)?.isPluginMarker() ?: false
}
