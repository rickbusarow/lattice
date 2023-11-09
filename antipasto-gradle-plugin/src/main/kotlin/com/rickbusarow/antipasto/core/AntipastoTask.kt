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

package com.rickbusarow.antipasto.core

import org.gradle.api.DefaultTask
import org.gradle.api.Task

@Suppress("UndocumentedPublicClass")
public abstract class DefaultAntipastoTask : DefaultTask(), AntipastoTask
public interface AntipastoTask : Task

public interface FixTask : AntipastoTask
public interface CheckTask : AntipastoTask

@Suppress("UndocumentedPublicClass")
public abstract class AntipastoCodeGeneratorTask : DefaultAntipastoTask()
