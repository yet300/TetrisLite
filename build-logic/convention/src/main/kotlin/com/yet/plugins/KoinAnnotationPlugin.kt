package com.yet.plugins

import libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KoinAnnotationPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("koin-compiler-plugin").get().get().pluginId)
        }

        extensions.getByType<KotlinMultiplatformExtension>().apply {
            sourceSets.getByName("commonMain").dependencies {
                implementation(libs.findLibrary("koin-core").get())
                implementation(libs.findLibrary("koin-annotation").get())
                implementation(libs.findLibrary("koin-jsr330").get())
            }
        }

    }
}