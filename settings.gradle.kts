rootProject.name = "TetrisLite"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        versionCatalogs {
            create("kotlinWrappers") {
                val wrappersVersion = "2025.10.11"
                from("org.jetbrains.kotlin-wrappers:kotlin-wrappers-catalog:$wrappersVersion")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")
include(":androidApp")
include(":shared")
include(":core")
include(":core:domain")
include(":core:data")
include(":core:database")
include(":core:common")

include(":feature")
include(":feature:root")
include(":feature:game")
include(":feature:settings")
include(":feature:history")
include(":feature:home")
include(":core:uikit")
include(":baselineprofile")
include(":webApp")
