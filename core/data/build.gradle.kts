plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.koin)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.database)

            implementation(libs.bundles.multiplatform.settings)

        }
        commonTest.dependencies {
            implementation(libs.bundles.testing)
        }
    }
}