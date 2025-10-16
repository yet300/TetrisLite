package com.yet.plugins

import libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class SqlDelightConventionPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("ksp").get().get().pluginId)
            apply(libs.findPlugin("sqlDelight").get().get().pluginId)
        }

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                commonMain.dependencies {
                    implementation(libs.findLibrary("sqldelight-coroutines-extensions").get())
                }

                commonTest.dependencies {
                    implementation(libs.findBundle("testing").get())
                }

                androidMain.dependencies {
                    implementation(libs.findLibrary("sqldelight-driver").get())
                    implementation(libs.findLibrary("sqldelight-android").get())
                }

                androidUnitTest.dependencies {
                    implementation(libs.findLibrary("sqldelight-driver").get())
                    implementation(libs.findBundle("android.test").get())
                }

                nativeMain.dependencies {
                    implementation(libs.findLibrary("sqldelight-native").get())
                }

                nativeTest.dependencies {
                    implementation(libs.findLibrary("sqldelight-native").get())
                }

                findByName("desktopMain")?.dependencies {
                    implementation(libs.findLibrary("sqldelight-driver").get())
                }

                findByName("desktopTest")?.dependencies {
                    implementation(libs.findLibrary("sqldelight-driver").get())
                }

                jsMain.dependencies {
                    implementation(libs.findLibrary("sqldelight-js").get())

                    implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
                    implementation(npm("sql.js", "1.6.2"))
                    implementation(devNpm("copy-webpack-plugin", "9.1.0"))
                    implementation(npm("@js-joda/timezone", "2.3.0"))
                }

                jsTest.dependencies {
                    implementation(libs.findLibrary("sqldelight-js").get())

                    implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
                    implementation(npm("sql.js", "1.6.2"))
                    implementation(devNpm("copy-webpack-plugin", "9.1.0"))
                    implementation(npm("@js-joda/timezone", "2.3.0"))
                }
            }
        }
    }
}