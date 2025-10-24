package com.yet.tetris.ui.view.game.rendering

import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.theme.VisualTheme

object ThemeColors {
    fun getTetrominoColor(
        type: TetrominoType,
        settings: GameSettings,
    ): String {
        val color =
            when (settings.themeConfig.visualTheme) {
                VisualTheme.CLASSIC ->
                    when (type) {
                        TetrominoType.I -> 0x00F0F0
                        TetrominoType.O -> 0xF0F000
                        TetrominoType.T -> 0xA000F0
                        TetrominoType.S -> 0x00F000
                        TetrominoType.Z -> 0xF00000
                        TetrominoType.J -> 0x0000F0
                        TetrominoType.L -> 0xF0A000
                    }

                VisualTheme.RETRO_GAMEBOY ->
                    when (type) {
                        TetrominoType.I -> 0x0F380F
                        TetrominoType.O -> 0x306230
                        TetrominoType.T -> 0x0F380F
                        TetrominoType.S -> 0x306230
                        TetrominoType.Z -> 0x0F380F
                        TetrominoType.J -> 0x306230
                        TetrominoType.L -> 0x0F380F
                    }

                VisualTheme.RETRO_NES ->
                    when (type) {
                        TetrominoType.I -> 0x00D8F8
                        TetrominoType.O -> 0xF8D800
                        TetrominoType.T -> 0xB800F8
                        TetrominoType.S -> 0x00F800
                        TetrominoType.Z -> 0xF80000
                        TetrominoType.J -> 0x0000F8
                        TetrominoType.L -> 0xF87800
                    }

                VisualTheme.NEON ->
                    when (type) {
                        TetrominoType.I -> 0x00FFFF
                        TetrominoType.O -> 0xFFFF00
                        TetrominoType.T -> 0xFF00FF
                        TetrominoType.S -> 0x00FF00
                        TetrominoType.Z -> 0xFF0066
                        TetrominoType.J -> 0x0066FF
                        TetrominoType.L -> 0xFF6600
                    }

                VisualTheme.PASTEL ->
                    when (type) {
                        TetrominoType.I -> 0xB4E7F5
                        TetrominoType.O -> 0xFFF4B4
                        TetrominoType.T -> 0xE5B4F5
                        TetrominoType.S -> 0xB4F5B4
                        TetrominoType.Z -> 0xF5B4B4
                        TetrominoType.J -> 0xB4B4F5
                        TetrominoType.L -> 0xF5D4B4
                    }

                VisualTheme.MONOCHROME ->
                    when (type) {
                        TetrominoType.I -> 0xFFFFFF
                        TetrominoType.O -> 0xE0E0E0
                        TetrominoType.T -> 0xC0C0C0
                        TetrominoType.S -> 0xA0A0A0
                        TetrominoType.Z -> 0x808080
                        TetrominoType.J -> 0x606060
                        TetrominoType.L -> 0x404040
                    }

                VisualTheme.OCEAN ->
                    when (type) {
                        TetrominoType.I -> 0x00CED1
                        TetrominoType.O -> 0x20B2AA
                        TetrominoType.T -> 0x4682B4
                        TetrominoType.S -> 0x5F9EA0
                        TetrominoType.Z -> 0x1E90FF
                        TetrominoType.J -> 0x0000CD
                        TetrominoType.L -> 0x000080
                    }

                VisualTheme.SUNSET ->
                    when (type) {
                        TetrominoType.I -> 0xFF6B6B
                        TetrominoType.O -> 0xFFD93D
                        TetrominoType.T -> 0xFF8C42
                        TetrominoType.S -> 0xFFA07A
                        TetrominoType.Z -> 0xFF69B4
                        TetrominoType.J -> 0xFF4500
                        TetrominoType.L -> 0xFF1493
                    }

                VisualTheme.FOREST ->
                    when (type) {
                        TetrominoType.I -> 0x228B22
                        TetrominoType.O -> 0x32CD32
                        TetrominoType.T -> 0x006400
                        TetrominoType.S -> 0x90EE90
                        TetrominoType.Z -> 0x2E8B57
                        TetrominoType.J -> 0x3CB371
                        TetrominoType.L -> 0x8FBC8F
                    }
            }
        return "#${color.toString(16).padStart(6, '0')}"
    }

    fun getTetrominoLightColor(
        type: TetrominoType,
        settings: GameSettings,
    ): String {
        val baseColor = getTetrominoColor(type, settings)
        return lightenColorHex(baseColor, 0.3)
    }

    fun getTetrominoDarkColor(
        type: TetrominoType,
        settings: GameSettings,
    ): String {
        val baseColor = getTetrominoColor(type, settings)
        return darkenColorHex(baseColor, 0.3)
    }

    fun getBackgroundColor(settings: GameSettings): String {
        val color =
            when (settings.themeConfig.visualTheme) {
                VisualTheme.CLASSIC -> 0x000000
                VisualTheme.RETRO_GAMEBOY -> 0x9BBC0F
                VisualTheme.RETRO_NES -> 0x000000
                VisualTheme.NEON -> 0x0A0A0A
                VisualTheme.PASTEL -> 0xF5F5DC
                VisualTheme.MONOCHROME -> 0x000000
                VisualTheme.OCEAN -> 0x001F3F
                VisualTheme.SUNSET -> 0x2C1810
                VisualTheme.FOREST -> 0x0D1F0D
            }
        return "#${color.toString(16).padStart(6, '0')}"
    }

    fun getGridColor(settings: GameSettings): String {
        val color =
            when (settings.themeConfig.visualTheme) {
                VisualTheme.CLASSIC -> 0x333333
                VisualTheme.RETRO_GAMEBOY -> 0x8BAC0F
                VisualTheme.RETRO_NES -> 0x404040
                VisualTheme.NEON -> 0x00FFFF
                VisualTheme.PASTEL -> 0xE0E0E0
                VisualTheme.MONOCHROME -> 0x404040
                VisualTheme.OCEAN -> 0x004080
                VisualTheme.SUNSET -> 0x804020
                VisualTheme.FOREST -> 0x1A3D1A
            }
        return "#${color.toString(16).padStart(6, '0')}"
    }

    private fun lightenColorHex(
        hex: String,
        factor: Double,
    ): String {
        val color = hex.removePrefix("#").toInt(16)
        val r = ((color shr 16) and 0xFF)
        val g = ((color shr 8) and 0xFF)
        val b = (color and 0xFF)

        val newR = (r + (255 - r) * factor).toInt().coerceIn(0, 255)
        val newG = (g + (255 - g) * factor).toInt().coerceIn(0, 255)
        val newB = (b + (255 - b) * factor).toInt().coerceIn(0, 255)

        return "#${((newR shl 16) or (newG shl 8) or newB).toString(16).padStart(6, '0')}"
    }

    private fun darkenColorHex(
        hex: String,
        factor: Double,
    ): String {
        val color = hex.removePrefix("#").toInt(16)
        val r = ((color shr 16) and 0xFF)
        val g = ((color shr 8) and 0xFF)
        val b = (color and 0xFF)

        val newR = (r * (1 - factor)).toInt().coerceIn(0, 255)
        val newG = (g * (1 - factor)).toInt().coerceIn(0, 255)
        val newB = (b * (1 - factor)).toInt().coerceIn(0, 255)

        return "#${((newR shl 16) or (newG shl 8) or newB).toString(16).padStart(6, '0')}"
    }
}
