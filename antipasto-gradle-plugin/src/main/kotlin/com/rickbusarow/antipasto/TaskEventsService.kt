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

package com.rickbusarow.antipasto

import com.rickbusarow.kgx.getOrPut
import org.gradle.api.NamedDomainObjectCollectionSchema.NamedDomainObjectSchema
import org.gradle.api.tasks.TaskCollection
import org.jetbrains.kotlin.gradle.plugin.extraProperties

/** @throws IllegalArgumentException if the task name is ambiguous when case is ignored */
internal fun TaskCollection<*>.namedOrNull(taskName: String): NamedDomainObjectSchema? {

  val namesLowercase = extraProperties.getOrPut("taskNamesLowercase") {
    collectionSchema.elements.groupBy { it.name.lowercase() }
  }

  val taskNameLowercase = taskName.lowercase()

  val matches = namesLowercase[taskNameLowercase] ?: return null

  val exactMatch = matches.singleOrNull { it.name == taskName }

  if (exactMatch != null) {
    return exactMatch
  }

  require(matches.size == 1) {
    "Task name '$taskName' is ambiguous.  " +
      "It matches multiple tasks: ${matches.map { it.name }}"
  }

  return matches.single()
}
