import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    androidLibrary {
        namespace = "com.yet.tetris.composeApp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        androidResources {
            enable = true
        }
    }

    js {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
        useEsModules()
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.uikit)

            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material3.adaptive.navigation.suite)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.components.ui.tooling.preview)

            implementation(libs.decompose.compose)

            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }

        jsMain.dependencies {
            implementation(
                project.dependencies.enforcedPlatform(
                    libs.jetbrains.kotlinWrappers.kotlinWrappersBom
                        .get(),
                ),
            )
            implementation(libs.kotlin.browser)
            //sql
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
            implementation(npm("@sqlite.org/sqlite-wasm", "3.43.2-build1"))
            implementation(libs.sqldelight.js)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.yet.tetris.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.yet.tetris"
            packageVersion = "1.0.0"
        }
    }
}
