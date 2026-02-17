package com.yet.plugins

import libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("composeMultiplatform").get().get().pluginId)
            apply(libs.findPlugin("composeCompiler").get().get().pluginId)
        }


        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                commonMain {
                    dependencies {
                        implementation(libs.findLibrary("compose-runtime").get())
                        implementation(libs.findLibrary("compose-foundation").get())
                        implementation(libs.findLibrary("compose-material3").get())
                        implementation(libs.findLibrary("compose-material-icons-extended").get())
                    }
                }
            }
        }
    }
}