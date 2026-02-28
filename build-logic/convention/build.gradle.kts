plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = "com.plugins.kotlinMultiplatformPlugin"
            implementationClass = "com.yet.plugins.KotlinMultiplatformPlugin"
        }
        register("composeMultiplatform") {
            id = "com.plugins.composeMultiplatform"
            implementationClass = "com.yet.plugins.ComposeMultiplatformPlugin"
        }

        register("sqlDelight") {
            id = "com.plugins.sqldelight"
            displayName = "SqlDelight"
            tags = listOf("sqldelight", "kmp")
            implementationClass = "com.yet.plugins.SqlDelightConventionPlugin"
        }
    }
}

group = "com.yet.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}
