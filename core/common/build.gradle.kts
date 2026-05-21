plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.koin)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.decompose)
            implementation(libs.bundles.mvi)
        }
    }
}