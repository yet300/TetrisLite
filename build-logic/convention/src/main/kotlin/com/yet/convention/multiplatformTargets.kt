import org.gradle.api.Project

internal data class MultiplatformTargetsConfig(
    val watchosEnabled: Boolean = true,
    val iosEnabled: Boolean = true,
    val macosEnabled: Boolean = true,
)

/**
 * Centralized per-module target setup, similar to ArkIvanov's gradle setup approach:
 * one defaults model + explicit module overrides.
 */
internal fun Project.multiplatformTargetsConfig(): MultiplatformTargetsConfig =
    when (path) {
        ":core:uikit" -> MultiplatformTargetsConfig(
            watchosEnabled = false,
            iosEnabled = false,
            macosEnabled = false,
        )

        else -> MultiplatformTargetsConfig()
    }
