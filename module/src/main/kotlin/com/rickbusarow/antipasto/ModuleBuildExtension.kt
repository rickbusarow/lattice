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

import com.rickbusarow.antipasto.conventions.AutoServiceExtension
import com.rickbusarow.antipasto.conventions.BuildLogicShadowExtensionHook
import com.rickbusarow.antipasto.conventions.KotlinExtension
import com.rickbusarow.antipasto.conventions.KotlinJvmExtension
import com.rickbusarow.antipasto.conventions.KotlinMultiplatformExtension
import com.rickbusarow.antipasto.conventions.KspExtension
import com.rickbusarow.antipasto.conventions.PokoExtension
import com.rickbusarow.antipasto.conventions.PublishingExtension
import com.rickbusarow.antipasto.conventions.SerializationExtension
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Suppress("UndocumentedPublicClass")
public abstract class RootExtension @Inject constructor(
  private val objects: ObjectFactory
) : CompositeHandler by objects.newInstance<DefaultCompositeHandler>(),
  AutoServiceExtension,
  BuildLogicShadowExtensionHook,
  KspExtension,
  PokoExtension,
  PublishingExtension,
  SerializationExtension

@Suppress("UndocumentedPublicClass")
public abstract class GradlePluginModuleExtension @Inject constructor(
  private val objects: ObjectFactory
) : AutoServiceExtension,
  BuildLogicShadowExtensionHook,
  KotlinJvmExtension,
  KspExtension,
  PokoExtension,
  PublishingExtension,
  SerializationExtension

@Suppress("UndocumentedPublicClass")
public abstract class KotlinJvmModuleExtension @Inject constructor(
  private val objects: ObjectFactory
) :
  AutoServiceExtension,
  BuildLogicShadowExtensionHook,
  KotlinJvmExtension,
  KspExtension,
  PokoExtension,
  PublishingExtension,
  SerializationExtension

@Suppress("UndocumentedPublicClass")
public abstract class KotlinMultiplatformModuleExtension @Inject constructor(
  private val objects: ObjectFactory
) :
  AutoServiceExtension,
  BuildLogicShadowExtensionHook,
  KotlinExtension,
  KotlinMultiplatformExtension,
  KspExtension,
  PokoExtension,
  PublishingExtension,
  SerializationExtension

@Suppress("UnusedPrivateMember") // no, it's used as a delegate
private inline fun <reified T : Any> ObjectFactory.newInstance(): T = newInstance(T::class.java)
