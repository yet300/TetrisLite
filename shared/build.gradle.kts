plugins {
    alias(libs.plugins.local.kotlin.multiplatform)
    alias(libs.plugins.local.koin)
}

kotlin {

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64(),
        watchosArm64(),
        watchosSimulatorArm64(),
        watchosX64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true

            export(projects.core.domain)

            export(libs.bundles.decompose)

            export(projects.feature.root)
            export(projects.feature.settings)
            export(projects.feature.game)
            export(projects.feature.home)
            export(projects.feature.history)
        }
    }

    js {
        outputModuleName = "shared"
        browser()
        binaries.library()
        generateTypeScriptDefinitions()
        compilerOptions {
            target = "es2015"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.common)
            api(projects.core.domain)

            api(projects.feature.root)
            api(projects.feature.settings)
            api(projects.feature.game)
            api(projects.feature.home)
            api(projects.feature.history)

            api(libs.bundles.decompose)
        }
    }
}

tasks.named("jsBrowserProductionWebpack") {
    dependsOn("jsProductionLibraryCompileSync")
}
