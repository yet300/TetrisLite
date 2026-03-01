plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.metro)
    alias(libs.plugins.local.sqlDelight)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
        }
    }
}

sqldelight {
    databases {
        create("TetrisLiteDatabase") {
            packageName.set("com.yet.tetris.database")
            generateAsync.set(true)
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack> {
    mainOutputFileName = "tetris.js"
}