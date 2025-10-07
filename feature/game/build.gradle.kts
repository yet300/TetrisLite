plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.koin)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.common)

            implementation(libs.bundles.decompose)
            implementation(libs.bundles.mvi)
        }
    }
}