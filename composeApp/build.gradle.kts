import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.baselineprofile)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
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
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(projects.core.uikit)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

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

android {
    namespace = "com.yet.tetris"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.yet.tetris"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
            excludes += "META-INF/com/android/build/gradle/*"
            excludes += "DebugProbesKt.bin"

            excludes += "**/*.kotlin_metadata"
            excludes += "**/*.version"
            excludes += "**/kotlin/**"

            // Exclude SQLite native libraries for non-Android platforms
            excludes += "org/sqlite/native/Mac/**"
            excludes += "org/sqlite/native/Windows/**"
            excludes += "org/sqlite/native/Linux/**"
            excludes += "org/sqlite/native/FreeBSD/**"
        }

        jniLibs {
            useLegacyPackaging = true
        }
    }

//    val keystorePropertiesFile = rootProject.file("keystore.properties")
//    val keystoreProperties = Properties()
//    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
//
//    signingConfigs {
//        create("release") {
//            storeFile = File(keystoreProperties["RELEASE_STORE_FILE"] as String)
//
//            keyPassword =  keystoreProperties["RELEASE_STORE_PASSWORD"] as String
//            keyAlias =  keystoreProperties["RELEASE_KEY_ALIAS"] as String
//            storePassword =  keystoreProperties["RELEASE_KEY_PASSWORD"] as String
//        }
//    }
    buildTypes {
        release {
            applicationIdSuffix = ".release"

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
//            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.profileinstaller)
    "baselineProfile"(project(":baselineprofile"))
    debugImplementation(compose.uiTooling)
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
