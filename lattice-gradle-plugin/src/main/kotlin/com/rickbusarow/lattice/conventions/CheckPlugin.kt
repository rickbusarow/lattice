/*
 * Copyright (C) 2024 Rick Busarow
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

import com.rickbusarow.kgx.applyOnce
import com.rickbusarow.lattice.core.CheckTask
import com.rickbusarow.lattice.core.DefaultLatticeTask
import com.rickbusarow.lattice.core.FixTask
import com.rickbusarow.lattice.deps.PluginIds
import kotlinx.validation.KotlinApiBuildTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin

public abstract class CheckPlugin : Plugin<Project> {

  override fun apply(target: Project) {

    target.plugins.applyOnce("base")

    val fix = target.tasks.register("fix", DefaultLatticeTask::class.java) { task ->

      task.group = "Verification"
      task.description = "Runs all auto-fix linting tasks"

      task.dependsOn(target.rootProject.tasks.withType(FixTask::class.java))
      task.dependsOn(target.rootProject.tasks.named("spotlessApply"))
      task.dependsOn(target.tasks.withType(KotlinApiBuildTask::class.java))

      if (target.plugins.hasPlugin(PluginIds.`dropbox-dependency-guard`)) {
        task.dependsOn(target.tasks.named("dependencyGuardBaseline"))
      }

      task.dependsOn(target.tasks.named("deleteEmptyDirs"))

      if (target.plugins.hasPlugin(PluginIds.`rickBusarow-moduleCheck`)) {
        task.dependsOn(target.tasks.named("moduleCheckAuto"))
      }
      task.dependsOn(target.tasks.named("ktlintFormat"))
    }

    // This is a convenience task which applies all available fixes before running `check`. Each
    // of the fixable linters use `mustRunAfter` to ensure that their auto-fix task runs before their
    // check-only task.
    target.tasks.register("checkFix", DefaultCheckTask::class.java) { task ->

      task.group = "Verification"
      task.description = "Runs all auto-fix linting tasks, then runs all of the normal :check task"

      task.dependsOn(target.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME))
      task.dependsOn(fix)
    }
  }
}

public abstract class DefaultFixTask : DefaultLatticeTask(), FixTask
public abstract class DefaultCheckTask : DefaultLatticeTask(), CheckTask
