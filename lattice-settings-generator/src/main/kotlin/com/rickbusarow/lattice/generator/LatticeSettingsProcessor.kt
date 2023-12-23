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

package com.rickbusarow.lattice.generator

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

/** */
class LatticeSettingsProcessor(
  environment: SymbolProcessorEnvironment
) : LatticeSymbolProcessor(environment) {

  override fun process(resolver: Resolver): List<KSAnnotated> {

    resolver.getSymbolsWithAnnotation(LatticeSettingsSchema::class.qualifiedName!!)
      .forEach { symbol ->

        val latticeSettings = parseClass(symbol as KSClassDeclaration, listOf("lattice"))

        val defaultCN = symbol.toClassName().default()

        val fs = FileSpec.builder(defaultCN)
          .addGeneratedBy()
          .addType(latticeSettings)
          .addAnnotation(Suppress::class, "AbsentOrWrongFileLicense")
          .build()

        codeGenerator.createNewFile(
          dependencies = Dependencies(false, symbol.containingFile!!),
          packageName = defaultCN.packageName,
          fileName = defaultCN.simpleName
        ).bufferedWriter().use { writer ->

          fs.toString()
            .replace("`internal`", "internal")
            .let(writer::write)
        }
      }

    return emptyList()
  }

  private fun parseClass(clazz: KSClassDeclaration, parentNames: List<String>): TypeSpec {

    clazz.check(clazz.hasSuperType(names.javaSerializable)) {
      "${clazz.toClassName()} must implement java.io.Serializable"
    }

    val nestedClasses = clazz.declarations
      .filterIsInstance<KSClassDeclaration>()

    val groupsByFqn = nestedClasses.associateBy { requireNotNull(it.qualifiedName).asString() }

    val groupTypes = groupsByFqn.keys

    val (groups, values) = clazz.getAllProperties()
      .partition { it.type.toTypeName().toString() in groupTypes }

    val clazzCN = clazz.toClassName()
    val defaultName = clazzCN.default()

    val builder = when {
      parentNames.size == 1 -> createTopLevelBuilder(
        defaultClassName = defaultName,
        docString = clazz.docString,
        interfaceClassName = clazzCN
      )

      else -> createNestedBuilder(
        defaultClassName = defaultName,
        docString = clazz.docString,
        interfaceClassName = clazzCN
      )
    }

    return builder
      .applyEach(values) { value ->
        addValueProperty(value, parentNames)
      }
      .applyEach(groups) { group ->

        val groupCN = group.type.resolve().toClassName()
        val groupCNString = groupCN.toString()
        val groupClass = groupsByFqn.getValue(groupCNString)

        val groupDefaultCN = groupCN.default()

        addGroupProperty(
          groupPropertyName = group.simpleName.asString(),
          groupDefaultCN = groupDefaultCN,
          docString = group.docString
        )

        addType(parseClass(groupClass, parentNames + group.simpleName.asString()))
      }
      .build()
  }

  private fun TypeSpec.Builder.addValueProperty(
    value: KSPropertyDeclaration,
    parentNames: List<String>
  ) = apply {
    val simpleName = value.simpleName.asString()
    val valueType = value.type.toTypeName() as ParameterizedTypeName

    val qualifiedPropertyName = parentNames.joinToString(".", postfix = ".$simpleName")

    val docString = value.docString?.trimIndent()

    // validateValuePropertyKdoc(
    //   value = value,
    //   docString = docString,
    //   simpleName = simpleName,
    //   qualifiedPropertyName = qualifiedPropertyName
    // )

    addProperty(
      PropertySpec.builder(simpleName, valueType, OVERRIDE)
        .maybeAddKdoc(docString)
        .initializer(
          buildCodeBlock {
            add("providers\n")
            add(".gradleProperty(%S)", qualifiedPropertyName)

            when (valueType.typeArguments.singleOrNull()) {
              names.boolean -> add("\n.map { it.toBoolean() }")
              names.int -> add("\n.map { it.toInt() }")
              else -> Unit
            }
          }
        )
        .build()
    )
  }

  private fun TypeSpec.Builder.addGroupProperty(
    groupPropertyName: String,
    groupDefaultCN: ClassName,
    docString: String?
  ) = addProperty(
    PropertySpec.builder(groupPropertyName, groupDefaultCN, OVERRIDE)
      .maybeAddKdoc(docString)
      .initializer("%T()", groupDefaultCN)
      .build()
  )

  private fun createTopLevelBuilder(
    defaultClassName: ClassName,
    docString: String?,
    interfaceClassName: ClassName
  ): TypeSpec.Builder {

    return TypeSpec.classBuilder(defaultClassName)
      .addModifiers(KModifier.OPEN)
      .maybeAddKdoc(docString)
      .addSuperinterface(interfaceClassName)
      .primaryConstructor(
        FunSpec.constructorBuilder()
          .addAnnotation(names.javaxInject)
          .addParameter("providers", names.gradleProviderFactory)
          .addParameter("target", names.gradleProject)
          .build()
      )
      .addProperty(
        PropertySpec.builder("providers", names.gradleProviderFactory)
          .initializer("providers")
          .addModifiers(KModifier.PRIVATE)
          .build()
      )
      .addProperty(
        PropertySpec.builder("target", names.gradleProject)
          .initializer("target")
          .addModifiers(KModifier.PRIVATE)
          .build()
      )
  }

  private fun createNestedBuilder(
    defaultClassName: ClassName,
    docString: String?,
    interfaceClassName: ClassName
  ): TypeSpec.Builder {
    return TypeSpec.classBuilder(defaultClassName)
      .addModifiers(KModifier.INNER)
      .maybeAddKdoc(docString)
      .addSuperinterface(interfaceClassName)
  }

  private fun ClassName.default() = ClassName(
    "$packageName.internal",
    simpleNames.map { "Default$it" }
  )
}

/** */
class LatticeSettingsProcessorProvider : SymbolProcessorProvider {
  override fun create(
    environment: SymbolProcessorEnvironment
  ): SymbolProcessor = LatticeSettingsProcessor(environment)
}
