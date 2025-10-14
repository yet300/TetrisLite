plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.koin)
    alias(libs.plugins.local.sqlDelight)
}

kotlin {

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            isStatic = true
        }
    }

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