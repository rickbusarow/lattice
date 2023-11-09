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

import com.rickbusarow.lattice.conventions.DokkatooConventionPlugin.Companion.DOKKATOO_HTML_TASK_NAME
import com.rickbusarow.lattice.core.GROUP
import com.rickbusarow.lattice.core.VERSION_NAME
import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar.Dokka
import com.vanniktech.maven.publish.KotlinJvm
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPom
import org.gradle.plugins.signing.Sign
import org.jetbrains.kotlin.gradle.utils.property
import javax.inject.Inject

public interface PublishingMavenHandler : java.io.Serializable {

  public val defaultPom: MavenPom

  public fun defaultPom(action: Action<in MavenPom>) {
    action.execute(defaultPom)
  }

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

  private val settings by property {
    objects.newInstance(Settings::class.java)
  }

  override val defaultPom: DefaultMavenPom by property {
    objects.newInstance(DefaultMavenPom::class.java)
      .also { pom ->
        pom.url.convention(settings.publishing.POM_URL)
        pom.name.convention(settings.publishing.POM_NAME)

        pom.description.convention(settings.publishing.POM_DESCRIPTION)
        pom.inceptionYear.convention(settings.publishing.POM_INCEPTION_YEAR)

        pom.licenses { licenseSpec ->
          licenseSpec.license { license ->
            license.name.convention(settings.publishing.POM_LICENSE_NAME)
            license.url.convention(settings.publishing.POM_LICENSE_URL)
            license.distribution.convention(settings.publishing.POM_LICENSE_DIST)
          }
        }

        pom.scm { scm ->
          scm.url.convention(settings.publishing.POM_SCM_URL)
          scm.connection.convention(settings.publishing.POM_SCM_CONNECTION)
          scm.developerConnection.convention(settings.publishing.POM_SCM_DEV_CONNECTION)
        }

        pom.developers { developerSpec ->
          developerSpec.developer { developer ->
            developer.id.convention(settings.publishing.POM_DEVELOPER_ID)
            developer.name.convention(settings.publishing.POM_DEVELOPER_NAME)
            developer.url.convention(settings.publishing.POM_DEVELOPER_URL)
          }
        }
      }
  }

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
    target.publishMaven(
      groupId = groupId,
      artifactId = artifactId,
      pomDescription = pomDescription,
      versionName = versionName
    )
  }

  @Suppress("UnstableApiUsage")
  private fun Project.publishMaven(
    publicationName: String = "maven",
    groupId: String,
    artifactId: String,
    pomDescription: String,
    versionName: String
  ) {

    val javadocJar = Dokka(taskName = DOKKATOO_HTML_TASK_NAME)

    when {
      // The plugin-publish plugin handles its artifacts
      pluginManager.hasPlugin("com.gradle.plugin-publish") -> {}

      // handle publishing plugins if they're not going to the plugin portal
      pluginManager.hasPlugin("java-gradle-plugin") -> {
        target.mavenPublishBaseExtension.configure(
          GradlePlugin(
            javadocJar = javadocJar,
            sourcesJar = true
          )
        )
      }

      else -> {
        target.mavenPublishBaseExtension.configure(
          KotlinJvm(
            javadocJar = javadocJar,
            sourcesJar = true
          )
        )
      }
    }

    val publications = gradlePublishingExtension.publications

    val mavenPublication = if (publications.names.contains(publicationName)) {
      publications.named(publicationName, MavenPublication::class.java)
    } else {
      publications.register(publicationName, MavenPublication::class.java)
    }

    mavenPublication.configure { publication ->
      publication.artifactId = artifactId
      publication.pom.description.set(pomDescription)
      publication.groupId = groupId
      publication.version = versionName

      val default = defaultPom

      publication.pom { mavenPom ->

        mavenPom.url.convention(default.url)
        mavenPom.name.convention(default.name)
        mavenPom.description.convention(default.description)
        mavenPom.inceptionYear.convention(default.inceptionYear)

        mavenPom.licenses { licenseSpec ->

          for (defaultLicense in default.licenses) {
            licenseSpec.license { license ->
              license.name.convention(defaultLicense.name)
              license.url.convention(defaultLicense.url)
              license.distribution.convention(defaultLicense.distribution)
            }
          }
        }

        mavenPom.scm { scm ->
          default.scm?.url?.let { scm.url.convention(it) }
          default.scm?.connection?.let { scm.connection.convention(it) }
          default.scm?.developerConnection?.let { scm.developerConnection.convention(it) }
        }

        mavenPom.developers { developerSpec ->
          for (defaultDeveloper in default.developers) {
            developerSpec.developer { developer ->
              developer.id.convention(defaultDeveloper.id)
              developer.name.convention(defaultDeveloper.name)
              developer.url.convention(defaultDeveloper.url)
            }
          }
        }
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
