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

package com.rickbusarow.lattice.curator

import com.rickbusarow.lattice.core.Color.Companion.colorized
import com.rickbusarow.lattice.core.Color.RED
import com.rickbusarow.antipasto.core.FixTask
import kotlinx.serialization.encodeToString
import org.gradle.api.GradleException
import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/** Evaluates all published artifacts in the project and writes the results to `/artifacts.json` */
public open class CuratorDumpTask @Inject constructor(
  projectLayout: ProjectLayout
) : AbstractCuratorTask(projectLayout), FixTask {

  init {
    description = "Parses the Maven artifact parameters for all modules " +
      "and writes them to artifacts.json"
    group = "other"
  }

  @TaskAction
  public fun run() {

    val ignored = baselineArtifacts.filter { it.isIgnored() }

    if (ignored.isNotEmpty()) {

      logger.error(ignoredArtifactsMessage(ignored).colorized(RED))
      throw GradleException("The artifacts baseline should only be updated from a macOS machine.")
    }

    val json = jsonAdapter.encodeToString(currentList.sorted())
      .let {
        if (it.endsWith("\n\n")) {
          it
        } else {
          it.plus("\n")
        }
      }

    reportFile.asFile.writeText(json)
  }
}
