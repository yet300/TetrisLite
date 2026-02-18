import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

private const val JDK_VERSION = 17
val JAVA_VERSION = JavaVersion.VERSION_17


internal fun Project.configureKotlinMultiplatform(
    extension: KotlinMultiplatformExtension,
) = extension.apply {
    val targetsConfig: MultiplatformTargetsConfig = multiplatformTargetsConfig()
    jvmToolchain(JDK_VERSION)

    extensions.configure<KotlinMultiplatformAndroidLibraryExtension>("android") {
        val moduleName = path.split(":").drop(2).joinToString(".")
        namespace = if (moduleName.isNotEmpty()) "com.yet.$moduleName" else "com.yet.tetris"

        compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
        minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()

    }

    jvm("desktop")

    js(IR) {
        browser()
        binaries.executable()
    }

    if (targetsConfig.iosEnabled) {
        iosArm64()
        iosX64()
        iosSimulatorArm64()
    }

    if (targetsConfig.macosEnabled) {
        macosX64()
        macosArm64()
    }

    if (targetsConfig.watchosEnabled) {
        watchosArm64()
        watchosSimulatorArm64()
        watchosX64()
    }

    applyDefaultHierarchyTemplate()

    configurations.configureEach {
        if (name.contains("desktop", ignoreCase = true) &&
            (name.endsWith("CompileClasspath") || name.endsWith("RuntimeClasspath"))
        ) {
            attributes.attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
        }
    }

    //common dependencies
    sourceSets.apply {
        commonMain {
            dependencies {
                api(libs.findLibrary("kotlinx-coroutines-core").get())
                api(libs.findLibrary("kotlinx-datetime").get())
                api(libs.findLibrary("kotlinx-serialization-json").get())
            }
        }
        commonTest.dependencies {
            implementation(libs.findBundle("testing").get())
        }
    }
}