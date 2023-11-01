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

@Suppress("ClassName", "ObjectPropertyNaming", "ConstPropertyName")
internal object PluginIds {

  internal object kotlin {
    /** `org.jetbrains.kotlin.android` */
    const val android = "org.jetbrains.kotlin.android"

    /** `org.jetbrains.kotlin.jvm` */
    const val jvm = "org.jetbrains.kotlin.jvm"

    /** `org.jetbrains.kotlin.multiplatform` */
    const val multiplatform = "org.jetbrains.kotlin.multiplatform"

    /** `org.jetbrains.kotlin.plugin.serialization` */
    const val serialization = "org.jetbrains.kotlin.plugin.serialization"
  }

  internal object vanniktech {
    internal object publish {
      /** `com.vanniktech.maven.publish.base` */
      const val base = "com.vanniktech.maven.publish.base"

      /** `com.vanniktech.maven.publish` */
      const val classic = "com.vanniktech.maven.publish"
    }
  }

  internal object rickBusarow {
    /** `com.rickbusarow.doks` */
    const val doks = "com.rickbusarow.doks"

    /** `com.rickbusarow.ktlint` */
    const val ktlint = "com.rickbusarow.ktlint"

    /** `com.rickbusarow.module-check` */
    const val moduleCheck = "com.rickbusarow.module-check"
  }

  internal object gmazzo {
    /** `com.github.gmazzo.buildconfig` */
    const val buildconfig = "com.github.gmazzo.buildconfig"
  }

  internal object adamko {
    /** `dev.adamko.dokkatoo` */
    const val dokkatoo = "dev.adamko.dokkatoo"

    /** `dev.adamko.dokkatoo-html` */
    const val `dokkatoo-html` = "dev.adamko.dokkatoo-html"
  }

  internal object autonomousapps {
    /** `com.autonomousapps.dependency-analysis` */
    const val dependencyAnalysis = "com.autonomousapps.dependency-analysis"
  }

  internal object breadmoirai {

    /** `com.github.breadmoirai.github-release` */
    const val `github-release` = "com.github.breadmoirai.github-release"
  }

  internal object gradle {

    /** `assembler-lang` */
    const val `assembler-lang` = "assembler-lang"

    /** `binary-base` */
    const val `binary-base` = "binary-base"

    /** `build-dashboard` */
    const val `build-dashboard` = "build-dashboard"

    /** `build-init` */
    const val `build-init` = "build-init"

    /** `c-lang` */
    const val `c-lang` = "c-lang"

    /** `clang-compiler` */
    const val `clang-compiler` = "clang-compiler"

    /** `component-base` */
    const val `component-base` = "component-base"

    /** `component-model-base` */
    const val `component-model-base` = "component-model-base"

    /** `cpp-application` */
    const val `cpp-application` = "cpp-application"

    /** `cpp-lang` */
    const val `cpp-lang` = "cpp-lang"

    /** `cpp-library` */
    const val `cpp-library` = "cpp-library"

    /** `cpp-unit-test` */
    const val `cpp-unit-test` = "cpp-unit-test"

    /** `cunit-test-suite` */
    const val `cunit-test-suite` = "cunit-test-suite"

    /** `eclipse-wtp` */
    const val `eclipse-wtp` = "eclipse-wtp"

    /** `gcc-compiler` */
    const val `gcc-compiler` = "gcc-compiler"

    /** `google-test-test-suite` */
    const val `google-test-test-suite` = "google-test-test-suite"

    /** `google-test` */
    const val `google-test` = "google-test"

    /** `groovy-base` */
    const val `groovy-base` = "groovy-base"

    /** `groovy-gradle-plugin` */
    const val `groovy-gradle-plugin` = "groovy-gradle-plugin"

    /** `help-tasks` */
    const val `help-tasks` = "help-tasks"

    /** `ivy-publish` */
    const val `ivy-publish` = "ivy-publish"

    /** `jacoco-report-aggregation` */
    const val `jacoco-report-aggregation` = "jacoco-report-aggregation"

    /** `java-base` */
    const val `java-base` = "java-base"

    /** `java-gradle-plugin` */
    const val `java-gradle-plugin` = "java-gradle-plugin"

    /** `java-library-distribution` */
    const val `java-library-distribution` = "java-library-distribution"

    /** `java-library` */
    const val `java-library` = "java-library"

    /** `java-platform` */
    const val `java-platform` = "java-platform"

    /** `java-test-fixtures` */
    const val `java-test-fixtures` = "java-test-fixtures"

    /** `jdk-toolchains` */
    const val `jdk-toolchains` = "jdk-toolchains"

    /** `jvm-ecosystem` */
    const val `jvm-ecosystem` = "jvm-ecosystem"

    /** `jvm-test-suite` */
    const val `jvm-test-suite` = "jvm-test-suite"

    /** `jvm-toolchain-management` */
    const val `jvm-toolchain-management` = "jvm-toolchain-management"

    /** `jvm-toolchains` */
    const val `jvm-toolchains` = "jvm-toolchains"

    /** `language-base` */
    const val `language-base` = "language-base"

    /** `lifecycle-base` */
    const val `lifecycle-base` = "lifecycle-base"

    /** `maven-publish` */
    const val `maven-publish` = "maven-publish"

    /** `microsoft-visual-cpp-compiler` */
    const val `microsoft-visual-cpp-compiler` = "microsoft-visual-cpp-compiler"

    /** `native-component-model` */
    const val `native-component-model` = "native-component-model"

    /** `native-component` */
    const val `native-component` = "native-component"

    /** `objective-c-lang` */
    const val `objective-c-lang` = "objective-c-lang"

    /** `objective-c` */
    const val `objective-c` = "objective-c"

    /** `objective-cpp-lang` */
    const val `objective-cpp-lang` = "objective-cpp-lang"

    /** `objective-cpp` */
    const val `objective-cpp` = "objective-cpp"

    /** `com.gradle.plugin-publish` */
    const val `plugin-publish` = "com.gradle.plugin-publish"

    /** `project-report` */
    const val `project-report` = "project-report"

    /** `project-reports` */
    const val `project-reports` = "project-reports"

    /** `reporting-base` */
    const val `reporting-base` = "reporting-base"

    /** `scala-base` */
    const val `scala-base` = "scala-base"

    /** `standard-tool-chains` */
    const val `standard-tool-chains` = "standard-tool-chains"

    /** `swift-application` */
    const val `swift-application` = "swift-application"

    /** `swift-library` */
    const val `swift-library` = "swift-library"

    /** `swiftpm-export` */
    const val `swiftpm-export` = "swiftpm-export"

    /** `test-report-aggregation` */
    const val `test-report-aggregation` = "test-report-aggregation"

    /** `test-suite-base` */
    const val `test-suite-base` = "test-suite-base"

    /** `version-catalog` */
    const val `version-catalog` = "version-catalog"

    /** `visual-studio` */
    const val `visual-studio` = "visual-studio"

    /** `windows-resource-script` */
    const val `windows-resource-script` = "windows-resource-script"

    /** `windows-resources` */
    const val `windows-resources` = "windows-resources"

    /** `antlr` */
    const val antlr = "antlr"

    /** `application` */
    const val application = "application"

    /** `assembler` */
    const val assembler = "assembler"

    /** `base` */
    const val base = "base"

    /** `c` */
    const val c = "c"

    /** `checkstyle` */
    const val checkstyle = "checkstyle"

    /** `codenarc` */
    const val codenarc = "codenarc"

    /** `cpp` */
    const val cpp = "cpp"

    /** `cunit` */
    const val cunit = "cunit"

    /** `distribution` */
    const val distribution = "distribution"

    /** `ear` */
    const val ear = "ear"

    /** `eclipse` */
    const val eclipse = "eclipse"

    /** `groovy` */
    const val groovy = "groovy"

    /** `idea` */
    const val idea = "idea"

    /** `jacoco` */
    const val jacoco = "jacoco"

    /** `java` */
    const val java = "java"

    /** `pmd` */
    const val pmd = "pmd"

    /** `publishing` */
    const val publishing = "publishing"

    /** `scala` */
    const val scala = "scala"

    /** `signing` */
    const val signing = "signing"

    /** `war` */
    const val war = "war"

    /** `wrapper` */
    const val wrapper = "wrapper"

    /** `xcode` */
    const val xcode = "xcode"

    /** `xctest` */
    const val xctest = "xctest"
  }

  internal object coditory {
    /** `com.coditory.integration-test` */
    const val `integration-test` = "com.coditory.integration-test"
  }

  internal object drewhamilton {
    /** `dev.drewhamilton.poko` */
    const val poko = "dev.drewhamilton.poko"
  }

  internal object johnrengelman {
    /** `com.github.johnrengelman.shadow` */
    const val shadow = "com.github.johnrengelman.shadow"
  }
}
