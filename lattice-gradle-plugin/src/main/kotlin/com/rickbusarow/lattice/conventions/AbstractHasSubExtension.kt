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
import com.rickbusarow.kgx.newInstanceLazy
import com.rickbusarow.lattice.core.HasObjectFactory
import com.rickbusarow.lattice.core.LatticeSettings
import com.rickbusarow.lattice.core.SubExtension
import com.rickbusarow.lattice.core.SubExtensionInternal
import com.rickbusarow.lattice.core.latticeSettings
import dev.drewhamilton.poko.Poko
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import javax.inject.Inject
import kotlin.reflect.KClass

@Poko
public open class SubExtensionRegistry @Inject constructor(
  private val objects: ObjectFactory
) {
  public val schema: MutableMap<String, SubExtensionElement<*>> =
    mutableMapOf<String, SubExtensionElement<*>>()

  internal inline fun <reified T : Any, reified R : T> bind(rClass: KClass<out R>): T {
    return objects.newInstance(rClass.java, this) as T
  }

  public fun <T : SubExtension<*>> register(
    name: String,
    type: Class<T>,
    instanceType: Class<out T>
  ): T {
    val instance = objects.newInstance(instanceType)
    schema[name] = SubExtensionElement(name, type, instance)
    return instance
  }

  @Poko
  public class SubExtensionElement<out T : SubExtension<*>>(
    public val name: String,
    public val type: Class<out T>,
    public val instance: T
  )
}

public abstract class AbstractHasSubExtension : HasObjectFactory {

  protected inline fun <reified T : SubExtension<T>> subExtension(): Lazy<T> {
    return objects.newInstanceLazy<T>()
  }

  protected fun <T : SubExtension<T>> subExtension(clazz: KClass<out T>): Lazy<T> {
    return objects.newInstanceLazy(clazz.java)
  }

  @Deprecated(
    "Use subExtension(clazz: KClass<out T>) instead.",
    ReplaceWith("subExtension(T::class)")
  )
  protected fun <T : SubExtension<T>> subExtension(clazz: Class<out T>): Lazy<T> {
    return objects.newInstanceLazy(clazz)
  }
}

public abstract class AbstractSubExtension @Inject constructor(
  protected val target: Project,
  final override val objects: ObjectFactory
) : SubExtensionInternal, ExtensionAware, ObjectFactory by objects {

  protected val latticeSettings: LatticeSettings by gradleLazy { target.latticeSettings }

  protected inline fun <reified T : SubExtension<T>> subExtension(): Lazy<T> {
    return objects.newInstanceLazy<T>()
  }

  protected fun <T : SubExtension<T>> subExtension(clazz: Class<out T>): Lazy<T> {
    return objects.newInstanceLazy(clazz)
  }
}
