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

package com.rickbusarow.antipasto.publishing

import com.rickbusarow.antipasto.conventions.DokkatooConventionPlugin.Companion.DOKKATOO_HTML_TASK_NAME
import com.rickbusarow.antipasto.conventions.applyBinaryCompatibility
import com.rickbusarow.antipasto.core.GITHUB_OWNER
import com.rickbusarow.antipasto.core.GITHUB_OWNER_REPO
import com.rickbusarow.antipasto.core.GITHUB_REPOSITORY
import com.rickbusarow.antipasto.core.GROUP
import com.rickbusarow.antipasto.core.VERSION_NAME
import com.rickbusarow.kgx.extras
import com.rickbusarow.kgx.getOrPut
import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.JavadocJar.Dokka
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.Sign
import javax.inject.Inject

public interface PublishingMavenHandler : java.io.Serializable {
  public fun publishMaven(artifactId: String, pomDescription: String)
  public fun publishMaven(
    groupId: String,
    artifactId: String,
    pomDescription: String,
    versionName: String
  )
}

public open class DefaultPublishingMavenHandler @Inject constructor(
  private val target: Project,
  private val objects: ObjectFactory
) : PublishingMavenHandler {

  override fun publishMaven(artifactId: String, pomDescription: String) {
    publishMaven(
      groupId = target.GROUP,
      artifactId = artifactId,
      pomDescription = pomDescription,
      versionName = target.VERSION_NAME
    )
  }

  override fun publishMaven(
    groupId: String,
    artifactId: String,
    pomDescription: String,
    versionName: String
  ) {

    target.version = versionName
    target.group = groupId
  }

  @Suppress("UnstableApiUsage")
  public fun Project.publishMaven(
    groupId: String,
    artifactId: String,
    pomDescription: String,
    versionName: String
  ) {

    target.version = versionName
    target.group = groupId

    applyBinaryCompatibility()

    val extension = target.mavenPublishBaseExtension

    extension.publishToMavenCentral(SonatypeHost.DEFAULT, automaticRelease = true)

    extension.signAllPublications()

    extension.pom { mavenPom ->
      mavenPom.description.set(pomDescription)
      mavenPom.name.set(artifactId)

      mavenPom.url.set(GITHUB_REPOSITORY)

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
          developer.name.set(property("DEVELOPER_NAME") as String)
          developer.url.set(property("DEVELOPER_URL") as String)
        }
      }
    }

    val sd = target.extras.getOrPut("skipDokka") { false }

    val javadocJar = if (sd) {
      JavadocJar.None()
    } else {
      Dokka(taskName = DOKKATOO_HTML_TASK_NAME)
    }

    when {
      // The plugin-publish plugin handles its artifacts
      pluginManager.hasPlugin("com.gradle.plugin-publish") -> {}

      // handle publishing plugins if they're not going to the plugin portal
      pluginManager.hasPlugin("java-gradle-plugin") -> {
        extension.configure(GradlePlugin(javadocJar = javadocJar, sourcesJar = true))
      }

      else -> {
        extension.configure(KotlinJvm(javadocJar = javadocJar, sourcesJar = true))
      }
    }

    extensions.configure(PublishingExtension::class.java) { publishingExtension ->
      publishingExtension.publications
        .withType(MavenPublication::class.java)
        .configureEach { publication ->
          publication.artifactId = artifactId
          publication.pom.description.set(pomDescription)
          publication.groupId = groupId
        }
    }

    // registerSnapshotVersionCheckTask()
    // configureSkipDokka()

    tasks.withType(Sign::class.java).configureEach {
      // it.notCompatibleWithConfigurationCache("")

      // skip signing for -SNAPSHOT publishing
      it.onlyIf { !(version as String).endsWith("SNAPSHOT") }
    }
  }
}
