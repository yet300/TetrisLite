plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.tab.home)
            implementation(projects.feature.tab.history)
            implementation(projects.feature.settings)

            implementation(projects.core.common)
            implementation(projects.core.domain)

            implementation(libs.koin.core)

            implementation(libs.bundles.decompose)
        }
    }
}