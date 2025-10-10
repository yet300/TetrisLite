plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.koin)
    alias(libs.plugins.sqlDelight)
}

kotlin {

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)

            implementation(libs.sqldelight.coroutines.extensions)
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.android)
            implementation(libs.sqldelight.driver)
        }

        nativeMain.dependencies {
            implementation(libs.sqldelight.native)
        }

        desktopMain.dependencies {
            implementation(libs.sqldelight.driver)
        }


        jsMain.dependencies {
            implementation(npm("@js-joda/timezone", "2.3.0"))

            implementation(libs.sqldelight.js)
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
            implementation(npm("sql.js", "1.6.2"))
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
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