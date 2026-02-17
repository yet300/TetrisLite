plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.compose.multiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)

            implementation(libs.compose.components.ui.tooling.preview)
        }
    }
}