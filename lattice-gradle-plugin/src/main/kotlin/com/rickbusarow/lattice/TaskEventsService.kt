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

package com.rickbusarow.lattice

import com.rickbusarow.kgx.extras
import com.rickbusarow.kgx.getOrPut
import org.gradle.api.Project

internal fun Project.taskWillResolveInAny(taskName: String): Boolean {
  return allprojects.any { it.taskWillResolve(taskName) }
}

internal fun Project.taskWillResolve(taskName: String): Boolean {

  val taskNamesLowercase = extras.getOrPut("taskNamesLowercase") {
    tasks.names.mapTo(mutableSetOf(), String::lowercase)
  }

  return tasks.names.contains(taskName) || taskNamesLowercase.contains(taskName.lowercase())
}
