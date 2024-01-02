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

import com.rickbusarow.kgx.library
import com.rickbusarow.kgx.libsCatalog
import com.rickbusarow.kgx.pluginId
import com.rickbusarow.kgx.version
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

public abstract class LibsGeneratorPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.tasks.register("generateLibs", LibsGeneratorTask::class.java) { task ->
      task.packageName.set("boogers")
      task.catalogProvider.set(target.libsCatalog)
    }
  }
}

public abstract class LibsGeneratorTask @Inject constructor() : DefaultTask() {

  @get:Input
  public abstract val packageName: Property<String>

  @get:Input
  public abstract val catalogProvider: Property<VersionCatalog>

  @TaskAction
  public fun generateLibs() {

    val catalog = catalogProvider.get()

    val versionValues = catalog.versionAliases
      .filterNot { it.startsWith("config.") }
      .map { alias ->
        val aliasAsVariableName = alias.asVariableName()

        aliasAsVariableName to catalog.version(alias)
      }

    val plugins = catalog.pluginAliases
      .map { alias ->
        println("%%%%%%%%%%%%%%%% original plugin alias -- $alias")
        val aliasAsVariableName = alias.asVariableName()
        aliasAsVariableName to catalog.pluginId(alias)
      }

    val libraries = catalog.libraryAliases
      .map { alias ->
        val aliasAsVariableName = alias.asVariableName()
        aliasAsVariableName to catalog.library(alias)
      }
      // TODO <Rick> delete me
      .forEach { (variable, library) ->
        println("################  $variable  --  $library")
      }

    val str = buildString {
      appendLine("package ${packageName.get()}")
      appendLine()
      appendLine("class Libs(")
      appendLine(
        versionValues.joinToString(separator = ",\n") { (alias, version) ->
          "  val $alias: String = \"$version\""
        }
      )
      appendLine(") : java.io.Serializable {")
      plugins.forEach {
        appendLine("  // ${it.first}  --  ${it.second}")
      }
      appendLine("}")
    }

    println("##########################################")
    println(str)
    println("##########################################")
  }

  private fun String.asVariableName(): String {
    return replace(".", "_")
    // return replace(Regex("\\.(.)"), "$1".uppercase(Locale.US))
  }
}
