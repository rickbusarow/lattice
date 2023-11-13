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

package com.rickbusarow.lattice.conventions

import com.rickbusarow.kgx.dependency
import com.rickbusarow.kgx.libsCatalog
import com.rickbusarow.ktlint.KtLintPlugin
import com.rickbusarow.ktlint.KtLintTask
import com.rickbusarow.lattice.core.PluginIds
import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class KtLintConventionPlugin : Plugin<Project> {

  override fun apply(target: Project) {

    target.plugins.apply(KtLintPlugin::class.java)

    target.dependencies
      .add("ktlint", target.libsCatalog.dependency("rickBusarow-ktrules"))

    target.tasks.withType(KtLintTask::class.java).configureEach { task ->

      target.allprojects
        .filter { it.plugins.hasPlugin(PluginIds.dropbox.dependency.guard) }
        .forEach { subproject ->
          task.mustRunAfter(subproject.tasks.named("dependencyGuard"))
          task.mustRunAfter(subproject.tasks.named("dependencyGuardBaseline"))
        }

      System.setProperty("ktrules.project_version", target.version.toString())

      task.mustRunAfter(
        target.tasks.withType(KotlinApiBuildTask::class.java),
        target.tasks.withType(KotlinApiCompareTask::class.java)
      )
    }
  }
}
