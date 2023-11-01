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
import com.rickbusarow.antipasto.core.PluginIds
import com.rickbusarow.kgx.extras
import com.rickbusarow.kgx.getOrPut
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import org.gradle.api.tasks.bundling.Jar
import org.gradle.plugins.signing.Sign
import kotlin.LazyThreadSafetyMode.NONE

/**
 */
public abstract class AntipastoPublishPlugin : Plugin<Project> {

  override fun apply(target: Project) {

    val GITHUB_OWNER = target.property("GITHUB_OWNER") as String
    val DEVELOPER_URL = target.property("DEVELOPER_URL") as String
    val DEVELOPER_NAME = target.property("DEVELOPER_NAME") as String
    val GITHUB_OWNER_REPO = target.property("GITHUB_OWNER_REPO") as String
    val GITHUB_REPOSITORY = target.property("GITHUB_REPOSITORY") as String

    target.plugins.apply(PluginIds.vanniktech.publish.base)

    val maven = target.mavenPublishBaseExtension

    maven.publishToMavenCentral(SonatypeHost.DEFAULT, automaticRelease = true)
    maven.signAllPublications()

    val gradlePluginPublish by lazy(NONE) { target.gradlePluginExtension }

    target.mavenPublications.configureEach { publication ->

      publication.configureCommon(
        GROUP = target.group as String,
        VERSION_CURRENT = target.version as String,
        GITHUB_OWNER = GITHUB_OWNER,
        GITHUB_OWNER_REPO = GITHUB_OWNER_REPO,
        GITHUB_REPOSITORY = GITHUB_REPOSITORY,
        DEVELOPER_NAME = DEVELOPER_NAME,
        DEVELOPER_URL = DEVELOPER_URL
      )

      if (publication.isPluginMarker()) {

        val plugin = gradlePluginPublish.plugins
          .named(publication.nameWithoutMarker())
        publication.pom.description.set(plugin.map { it.description })
      }
    }

    target.tasks.withType(Jar::class.java).configureEach {
      val skipDokka = target.extras.getOrPut("skipDokka") { false }

      if (it.name == "javadocJar" && !skipDokka) {
        it.archiveClassifier.set("javadoc")
        it.from(target.tasks.named(DOKKATOO_HTML_TASK_NAME))
      }
    }
    target.tasks.withType(GenerateModuleMetadata::class.java).configureEach {
      it.mustRunAfter("javadocJar")
    }
    target.tasks.withType(AbstractPublishToMaven::class.java).configureEach {
      it.mustRunAfter(target.tasks.withType(Jar::class.java))
    }
    target.tasks.withType(Sign::class.java).configureEach {
      it.mustRunAfter(target.tasks.withType(Jar::class.java))
    }

    // @OptIn(InternalGradleApiAccess::class)
    // target.tasks.whenElementKnown { ele ->
    //
    //   if (AbstractPublishToMaven::class.java.isAssignableFrom(ele.elementType)) {
    //     target.tasks.register(ele.elementName + "NoDokka", AntipastoTask::class.java) {
    //       it.dependsOn(ele.elementName)
    //       target.extras.set("skipDokka", true)
    //     }
    //   }
    // }
  }

  private fun MavenPublication.configureCommon(
    GROUP: String,
    VERSION_CURRENT: String,
    GITHUB_OWNER: String,
    GITHUB_OWNER_REPO: String,
    GITHUB_REPOSITORY: String,
    DEVELOPER_NAME: String,
    DEVELOPER_URL: String
  ) {

    groupId = GROUP
    version = VERSION_CURRENT

    pom { mavenPom ->
      mavenPom.url.set(GITHUB_REPOSITORY)

      mavenPom.licenses { licenseSpec ->
        licenseSpec.license { license ->
          license.name.set("The Apache Software License, Version 2.0")
          license.url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
          license.distribution.set("repo")
        }
      }
      mavenPom.scm { scm ->
        scm.url.set(GITHUB_REPOSITORY)
        scm.connection.set("scm:git:git://github.com/$GITHUB_OWNER_REPO.git")
        scm.developerConnection.set("scm:git:ssh://git@github.com/$GITHUB_OWNER_REPO.git")
      }
      mavenPom.developers { developerSpec ->
        developerSpec.developer { developer ->
          developer.id.set(GITHUB_OWNER)
          developer.name.set(DEVELOPER_NAME)
          developer.url.set(DEVELOPER_URL)
        }
      }
    }
  }
}
