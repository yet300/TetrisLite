plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.metro)
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.database)

            implementation(libs.bundles.multiplatform.settings)

        }
        androidUnitTest.dependencies {
            implementation(libs.bundles.android.test)
        }

        commonTest.dependencies {
            implementation(libs.bundles.testing)
            implementation(projects.core.database)
            implementation(libs.multiplatform.settings.test)
            implementation(libs.multiplatform.settings.coroutines)
        }
        jsMain.dependencies {
            implementation(project.dependencies.enforcedPlatform(libs.jetbrains.kotlinWrappers.kotlinWrappersBom.get()))
            implementation(libs.kotlin.browser)
        }
    }
}