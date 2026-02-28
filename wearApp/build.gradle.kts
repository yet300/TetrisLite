plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.yet.tetris.wear"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.yet.tetris.wear"
        minSdk = 28
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.shared)
    implementation(projects.core.uikit)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.decompose.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.toolingPreview)
    debugImplementation(libs.androidx.compose.ui.tooling)


    implementation(libs.androidx.wear.compose.foundation)
    implementation(libs.androidx.wear.compose.navigation)
}
