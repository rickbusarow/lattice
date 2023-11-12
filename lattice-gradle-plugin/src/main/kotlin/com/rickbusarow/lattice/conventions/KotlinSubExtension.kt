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

package com.rickbusarow.lattice.conventions

import com.rickbusarow.kgx.gradleLazy
import com.rickbusarow.kgx.property
import com.rickbusarow.lattice.core.SubExtension
import com.rickbusarow.lattice.core.SubExtensionInternal
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

public interface HasKotlinSubExtension : java.io.Serializable {
  public val kotlin: KotlinSubExtension
}

public interface HasKotlinJvmSubExtension : HasKotlinSubExtension {
  public override val kotlin: KotlinJvmSubExtension
}

public interface HasKotlinMultiplatformSubExtension : HasKotlinSubExtension {
  public override val kotlin: KotlinMultiplatformSubExtension
}

public abstract class DefaultHasKotlinSubExtension @Inject constructor(
  final override val objects: ObjectFactory
) : AbstractHasSubExtension(), HasKotlinSubExtension {

  override val kotlin: KotlinSubExtension by subExtension(DefaultKotlinSubExtension::class)
}

public abstract class DefaultHasKotlinJvmSubExtension @Inject constructor(
  objects: ObjectFactory
) : DefaultHasKotlinSubExtension(objects), HasKotlinJvmSubExtension {

  override val kotlin: KotlinJvmSubExtension by gradleLazy {
    objects.newInstance(DefaultKotlinJvmSubExtension::class.java)
  }
}

public abstract class DefaultHasKotlinMultiplatformSubExtension @Inject constructor(
  objects: ObjectFactory
) : DefaultHasKotlinSubExtension(objects), HasKotlinMultiplatformSubExtension {

  override val kotlin: KotlinMultiplatformSubExtension by gradleLazy {
    objects.newInstance(DefaultKotlinMultiplatformSubExtension::class.java)
  }
}

public interface KotlinSubExtension : SubExtension<KotlinSubExtension> {
  public val apiLevel: Property<String>
  public val allWarningsAsErrors: Property<Boolean>
  public val explicitApi: Property<Boolean>
}

public abstract class DefaultKotlinSubExtension @Inject constructor(
  target: Project,
  objects: ObjectFactory
) : AbstractSubExtension(target, objects), KotlinSubExtension, SubExtensionInternal {

  override val apiLevel: Property<String> =
    objects.property(convention = latticeSettings.kotlin.apiLevel)

  override val allWarningsAsErrors: Property<Boolean> =
    objects.property(latticeSettings.kotlin.allWarningsAsErrors)

  override val explicitApi: Property<Boolean> =
    objects.property(latticeSettings.kotlin.explicitApi)
}

public interface KotlinJvmSubExtension : KotlinSubExtension
public abstract class DefaultKotlinJvmSubExtension @Inject constructor(
  target: Project,
  objects: ObjectFactory
) : DefaultKotlinSubExtension(target, objects),
  KotlinJvmSubExtension,
  SubExtensionInternal

public interface KotlinMultiplatformSubExtension : KotlinSubExtension
public abstract class DefaultKotlinMultiplatformSubExtension @Inject constructor(
  target: Project,
  objects: ObjectFactory
) : DefaultKotlinSubExtension(target, objects),
  KotlinMultiplatformSubExtension,
  SubExtensionInternal
