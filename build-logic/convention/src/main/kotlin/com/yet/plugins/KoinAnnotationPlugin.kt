package com.yet.plugins

import libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import com.google.devtools.ksp.gradle.KspExtension

class KoinAnnotationPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("ksp").get().get().pluginId)
        }

        extensions.getByType<KotlinMultiplatformExtension>().apply {
            sourceSets.getByName("commonMain").dependencies {
                implementation(libs.findLibrary("koin-core").get())
                implementation(libs.findLibrary("koin-annotation").get())
            }
            sourceSets.named("commonMain").configure {
                kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            }
        }

        dependencies {
            add("kspCommonMainMetadata", libs.findLibrary("koin-annotation-compiler").get())
            add("kspAndroid", libs.findLibrary("koin-annotation-compiler").get())
            add("kspIosX64", libs.findLibrary("koin-annotation-compiler").get())
            add("kspIosArm64", libs.findLibrary("koin-annotation-compiler").get())
            add("kspIosSimulatorArm64", libs.findLibrary("koin-annotation-compiler").get())

        }

        extensions.getByType<KspExtension>().apply {
            arg("KOIN_CONFIG_CHECK", "true")
        }

        tasks.matching { it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata" }.configureEach {
            dependsOn("kspCommonMainKotlinMetadata")
        }

        tasks.named("runKtlintCheckOverCommonMainSourceSet") {
            dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
        }

        tasks.named("runKtlintFormatOverCommonMainSourceSet") {
            dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
        }
    }
}