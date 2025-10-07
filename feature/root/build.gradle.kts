plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.common)

            implementation(projects.feature.game)
            implementation(projects.feature.home)

            implementation(libs.bundles.decompose)
            implementation(libs.bundles.reaktive)
            implementation(libs.koin.core)
        }
    }
}