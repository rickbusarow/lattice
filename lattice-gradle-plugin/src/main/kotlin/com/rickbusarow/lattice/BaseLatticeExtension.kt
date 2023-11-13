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

import com.rickbusarow.kgx.newInstance
import com.rickbusarow.lattice.composite.DefaultHasCompositeSubExtension
import com.rickbusarow.lattice.composite.HasCompositeSubExtension
import com.rickbusarow.lattice.conventions.AutoServiceExtension
import com.rickbusarow.lattice.conventions.BuildLogicShadowExtensionHook
import com.rickbusarow.lattice.conventions.DefaultHasGitHubSubExtension
import com.rickbusarow.lattice.conventions.DefaultHasJavaSubExtension
import com.rickbusarow.lattice.conventions.DefaultHasKotlinJvmSubExtension
import com.rickbusarow.lattice.conventions.DefaultHasKotlinSubExtension
import com.rickbusarow.lattice.conventions.HasGitHubSubExtension
import com.rickbusarow.lattice.conventions.HasJavaSubExtension
import com.rickbusarow.lattice.conventions.HasKotlinJvmSubExtension
import com.rickbusarow.lattice.conventions.HasKotlinSubExtension
import com.rickbusarow.lattice.conventions.KotlinExtension
import com.rickbusarow.lattice.conventions.KotlinJvmExtension
import com.rickbusarow.lattice.conventions.KotlinMultiplatformExtension
import com.rickbusarow.lattice.conventions.KspExtension
import com.rickbusarow.lattice.conventions.PokoExtension
import com.rickbusarow.lattice.conventions.SerializationExtension
import com.rickbusarow.lattice.conventions.SubExtensionRegistry
import com.rickbusarow.lattice.dokka.DefaultHasDokkaSubExtension
import com.rickbusarow.lattice.dokka.HasDokkaSubExtension
import com.rickbusarow.lattice.publishing.DefaultHasPublishingMavenSubExtension
import com.rickbusarow.lattice.publishing.DefaultPublishingGradlePluginHandler
import com.rickbusarow.lattice.publishing.HasPublishingMavenSubExtension
import com.rickbusarow.lattice.publishing.PublishingGradlePluginHandler
import modulecheck.utils.cast
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.ListProperty
import javax.inject.Inject
import kotlin.reflect.KClass

public interface LatticeExtensionInternal : ExtensionAware {
  public val objects: ObjectFactory
  public val target: Project
}

public abstract class BaseLatticeExtension @Inject constructor(
  private val target: Project,
  private val objects: ObjectFactory
) : ExtensionAware,
  CoreLatticeSettings by objects.newInstance<DefaultCoreLatticeSettings>()

public open class RootExtension @Inject constructor(
  private val subExtensionRegistry: SubExtensionRegistry,
  target: Project,
  objects: ObjectFactory
) : BaseLatticeExtension(target, objects),
  HasCompositeSubExtension by objects.newInstance<DefaultHasCompositeSubExtension>(),
  HasDokkaSubExtension by subExtensionRegistry.bind(DefaultHasDokkaSubExtension::class),
  // HasDokkaSubExtension by objects.newInstance<DefaultHasDokkaSubExtension>(subExtensionRegistry),
  HasGitHubSubExtension by objects.newInstance<DefaultHasGitHubSubExtension>(),
  HasJavaSubExtension by objects.newInstance<DefaultHasJavaSubExtension>(),
  HasKotlinSubExtension by objects.newInstance<DefaultHasKotlinSubExtension>(),
  AutoServiceExtension,
  BuildLogicShadowExtensionHook,
  KspExtension,
  PokoExtension,
  SerializationExtension {

  init {
    subExtensionRegistry.schema.values.forEach { ele ->
      extensions.add(ele.type, ele.name, ele.instance.cast())
    }
  }

  public val bananas: Bananas = objects.newInstance()

  private inline fun <reified T : Any, reified R : T> subby(
    propertyName: String,
    realClazz: KClass<out R>
  ): T = extensions.create(propertyName, T::class.java)
}

public interface Bananas {
  public val bunches: ListProperty<String>
}

public abstract class GradlePluginModuleExtension @Inject constructor(
  private val subExtensionRegistry: SubExtensionRegistry,
  target: Project,
  objects: ObjectFactory
) : BaseLatticeExtension(target, objects),
  HasDokkaSubExtension by objects.newInstance<DefaultHasDokkaSubExtension>(subExtensionRegistry),
  HasGitHubSubExtension by objects.newInstance<DefaultHasGitHubSubExtension>(),
  HasJavaSubExtension by objects.newInstance<DefaultHasJavaSubExtension>(),
  HasKotlinJvmSubExtension by objects.newInstance<DefaultHasKotlinJvmSubExtension>(),
  HasPublishingMavenSubExtension by objects.newInstance<DefaultHasPublishingMavenSubExtension>(),
  PublishingGradlePluginHandler by objects.newInstance<DefaultPublishingGradlePluginHandler>(),
  AutoServiceExtension,
  BuildLogicShadowExtensionHook,
  KotlinJvmExtension,
  KspExtension,
  PokoExtension,
  SerializationExtension {

  init {
    subExtensionRegistry.schema.values.forEach { ele ->
      extensions.add(ele.type, ele.name, ele.instance.cast())
    }
  }
}

public abstract class KotlinJvmModuleExtension @Inject constructor(
  private val subExtensionRegistry: SubExtensionRegistry,
  target: Project,
  objects: ObjectFactory
) : BaseLatticeExtension(target, objects),
  HasPublishingMavenSubExtension by objects.newInstance<DefaultHasPublishingMavenSubExtension>(),
  HasDokkaSubExtension by objects.newInstance<DefaultHasDokkaSubExtension>(subExtensionRegistry),
  AutoServiceExtension,
  BuildLogicShadowExtensionHook,
  KotlinJvmExtension,
  KspExtension,
  PokoExtension,
  SerializationExtension {

  init {
    subExtensionRegistry.schema.values.forEach { ele ->
      extensions.add(ele.type, ele.name, ele.instance.cast())
    }
  }
}

public abstract class KotlinMultiplatformModuleExtension @Inject constructor(
  target: Project,
  objects: ObjectFactory
) : BaseLatticeExtension(target, objects),
  AutoServiceExtension,
  BuildLogicShadowExtensionHook,
  KotlinExtension,
  KotlinMultiplatformExtension,
  KspExtension,
  PokoExtension,
  SerializationExtension
