plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
}

kotlin {
    js(IR) {
        browser {
            useCommonJs()
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(projects.shared)

            implementation(kotlinWrappers.react)
            implementation(kotlinWrappers.reactDom)
            implementation(kotlinWrappers.emotion.styled)
            implementation(kotlinWrappers.tanstack.reactTable)

            implementation(kotlinWrappers.mui.material)
            implementation(kotlinWrappers.mui.iconsMaterial)
            implementation(kotlinWrappers.mui.base)

            implementation(kotlinWrappers.browser)

            // Decompose
            implementation(libs.decompose)
            implementation(libs.essenty.lifecycle)
            implementation(libs.essenty.stateKeeper)


            //sql
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
            implementation(npm("@sqlite.org/sqlite-wasm", "3.43.2-build1"))
            implementation(libs.sqldelight.js)
        }

    }
}
