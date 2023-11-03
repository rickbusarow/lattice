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
import com.rickbusarow.antipasto.core.PluginIds
import com.rickbusarow.kgx.extras
import com.rickbusarow.kgx.getOrPut
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import org.gradle.api.tasks.bundling.Jar
import org.gradle.plugins.signing.Sign
import kotlin.LazyThreadSafetyMode.NONE

/** */
public abstract class AntipastoPublishPlugin : Plugin<Project> {

  override fun apply(target: Project) {

    target.plugins.apply(PluginIds.vanniktech.publish.base)

    target.applyBinaryCompatibility()

    val maven = target.mavenPublishBaseExtension

    maven.publishToMavenCentral(SonatypeHost.DEFAULT, automaticRelease = true)
    maven.signAllPublications()

    val gradlePluginPublish by lazy(NONE) { target.gradlePluginExtension }

    target.mavenPublications.configureEach { publication ->

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
  }
}
