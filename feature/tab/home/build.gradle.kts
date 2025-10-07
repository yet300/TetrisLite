plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.koin)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.domain)

            implementation(libs.bundles.decompose)
            implementation(libs.bundles.mvi)
        }
        
        commonTest.dependencies {
            implementation(libs.bundles.testing)
        }
    }
}