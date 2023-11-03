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

import com.rickbusarow.antipasto.core.PluginIds
import org.gradle.api.Project

internal fun Project.applyBinaryCompatibility() {

  if (!plugins.hasPlugin(PluginIds.kotlinx.binary.compatibility.validator)) {
    // plugins.apply(PluginIds.kotlinx.binary.compatibility.validator)
    //
    // extensions.configure(ApiValidationExtension::class.java) { extension ->
    //
    //   // Packages that are excluded from public API dumps even if they contain public API
    //   extension.ignoredPackages = mutableSetOf("sample", "samples")
    //
    //   // Subprojects that are excluded from API validation
    //   extension.ignoredProjects = mutableSetOf()
    // }
    //
    // tasks.withType(KotlinApiCompareTask::class.java).configureEach { task ->
    //   task.mustRunAfter(tasks.withType(KotlinApiBuildTask::class.java))
    // }
  }
}
