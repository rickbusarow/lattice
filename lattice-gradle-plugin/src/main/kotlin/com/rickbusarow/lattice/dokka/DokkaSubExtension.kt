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

package com.rickbusarow.lattice.dokka

import com.rickbusarow.kgx.property
import com.rickbusarow.lattice.conventions.AbstractHasSubExtension
import com.rickbusarow.lattice.conventions.AbstractSubExtension
import com.rickbusarow.lattice.conventions.SubExtensionRegistry
import com.rickbusarow.lattice.core.SubExtension
import com.rickbusarow.lattice.core.SubExtensionInternal
import com.rickbusarow.lattice.core.latticeSettings
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import javax.inject.Inject

public interface HasDokkaSubExtension :
  ExtensionAware,
  java.io.Serializable {

  /**
   * This is a kdoc comment written in the interface
   */
  public val dokka: DokkaSubExtension
  // public fun dokka(action: Action<in DokkaSubExtension>) {
  //   action.execute(dokka)
  // }
}

public abstract class DefaultHasDokkaSubExtension @Inject constructor(
  subExtensionRegistry: SubExtensionRegistry,
  override val objects: ObjectFactory
) : AbstractHasSubExtension(), HasDokkaSubExtension {

  override val dokka: DokkaSubExtension = subExtensionRegistry.register(
    name = "dokka",
    type = DokkaSubExtension::class.java,
    instanceType = DefaultDokkaSubExtension::class.java
  )
  // override val dokka: DokkaSubExtension by subExtension(DefaultDokkaSubExtension::class)
}

public interface DokkaSubExtension : SubExtension<DokkaSubExtension> {
  public val dokkaVersion: Property<String>

  public val generateTaskWorkerMinHeapSize: Property<String>
  public val generateTaskWorkerMaxHeapSize: Property<String>
}

public abstract class DefaultDokkaSubExtension @Inject constructor(
  target: Project,
  objects: ObjectFactory
) : AbstractSubExtension(target, objects),
  DokkaSubExtension,
  SubExtensionInternal {

  override val dokkaVersion: Property<String> = property(target.latticeSettings.versions.dokka)
  override val generateTaskWorkerMinHeapSize: Property<String> = property("512m")
  override val generateTaskWorkerMaxHeapSize: Property<String> = property("1024m")
}
