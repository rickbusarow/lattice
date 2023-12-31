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

import com.rickbusarow.lattice.conventions.AutoServiceExtension
import com.rickbusarow.lattice.conventions.BuildLogicShadowExtensionHook
import com.rickbusarow.lattice.conventions.KotlinExtension
import com.rickbusarow.lattice.conventions.KotlinJvmExtension
import com.rickbusarow.lattice.conventions.KotlinMultiplatformExtension
import com.rickbusarow.lattice.conventions.KspExtension
import com.rickbusarow.lattice.conventions.PokoExtension
import com.rickbusarow.lattice.conventions.PublishingExtension
import com.rickbusarow.lattice.conventions.SerializationExtension
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Suppress("UndocumentedPublicClass")
public abstract class RootExtension @Inject constructor(
  private val objects: ObjectFactory
) : CompositeHandler by objects<DefaultCompositeHandler>(),
  AutoServiceExtension,
  BuildLogicShadowExtensionHook,
  KspExtension,
  PokoExtension,
  PublishingExtension,
  SerializationExtension

@Suppress("UndocumentedPublicClass")
public abstract class KotlinJvmModuleExtension :
  AutoServiceExtension,
  BuildLogicShadowExtensionHook,
  KotlinJvmExtension,
  KspExtension,
  PokoExtension,
  PublishingExtension,
  SerializationExtension

@Suppress("UndocumentedPublicClass")
public abstract class KotlinMultiplatformModuleExtension :
  AutoServiceExtension,
  BuildLogicShadowExtensionHook,
  KotlinExtension,
  KotlinMultiplatformExtension,
  KspExtension,
  PokoExtension,
  PublishingExtension,
  SerializationExtension

@Suppress("UnusedPrivateMember") // no, it's used as a delegate
private inline operator fun <reified T : Any> ObjectFactory.invoke(): T = newInstance(T::class.java)
