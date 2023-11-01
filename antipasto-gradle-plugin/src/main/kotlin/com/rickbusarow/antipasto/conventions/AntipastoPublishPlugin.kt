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

import com.rickbusarow.kgx.extras
import com.rickbusarow.kgx.getOrPut
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *
 */
public abstract class AntipastoPublishPlugin : Plugin<Project> {

  override fun apply(target: Project) {

    val skipDokka = target.extras.getOrPut("skipDokka") { false }

    val GITHUB_OWNER = target.extras.get("GITHUB_OWNER") as String
    val DEVELOPER_URL = target.extras.get("DEVELOPER_URL") as String
    val DEVELOPER_NAME = target.extras.get("DEVELOPER_NAME") as String
    val GITHUB_OWNER_REPO = target.extras.get("GITHUB_OWNER_REPO") as String
    val GITHUB_REPOSITORY = target.extras.get("GITHUB_REPOSITORY") as String

    target.plugins.apply("com.vanniktech.maven.publish.base")
  }
}
