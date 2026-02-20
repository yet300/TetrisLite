package com.yet.tetris.ui.view.settings.components

import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.VisualTheme
import com.yet.tetris.ui.strings.Strings
import mui.material.Box
import mui.material.Chip
import mui.material.ChipColor
import mui.material.ChipVariant
import mui.material.Stack
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.create
import react.dom.html.ReactHTML.div
import web.cssom.Display
import web.cssom.FlexWrap
import web.cssom.rem

external interface ThemeSelectorProps : Props {
    var visualTheme: VisualTheme
    var pieceStyle: PieceStyle
    var onVisualThemeChanged: (VisualTheme) -> Unit
    var onPieceStyleChanged: (PieceStyle) -> Unit
}

val ThemeSelector =
    FC<ThemeSelectorProps> { props ->
        Stack {
            spacing = responsive(4)

            // Visual Theme
            SettingsSection {
                title = Strings.VISUAL_THEME

                Box {
                    sx {
                        display = Display.flex
                        flexWrap = FlexWrap.wrap
                        gap = 1.rem
                    }

                    VisualTheme.entries.forEach { theme ->
                        Chip {
                            key = theme.name
                            label =
                                div.create {
                                    +theme.name.lowercase().replaceFirstChar { it.uppercase() }
                                }
                            variant =
                                if (props.visualTheme == theme) {
                                    ChipVariant.filled
                                } else {
                                    ChipVariant.outlined
                                }
                            color =
                                if (props.visualTheme == theme) {
                                    ChipColor.primary
                                } else {
                                    ChipColor.default
                                }
                            onClick = { props.onVisualThemeChanged(theme) }
                        }
                    }
                }
            }

            // Piece Style
            SettingsSection {
                title = Strings.PIECE_STYLE

                Box {
                    sx {
                        display = Display.flex
                        flexWrap = FlexWrap.wrap
                        gap = 1.rem
                    }

                    PieceStyle.entries.forEach { style ->
                        Chip {
                            key = style.name
                            label =
                                div.create {
                                    +style.name.lowercase().replaceFirstChar { it.uppercase() }
                                }
                            variant =
                                if (props.pieceStyle == style) {
                                    ChipVariant.filled
                                } else {
                                    ChipVariant.outlined
                                }
                            color =
                                if (props.pieceStyle == style) {
                                    ChipColor.primary
                                } else {
                                    ChipColor.default
                                }
                            onClick = { props.onPieceStyleChanged(style) }
                        }
                    }
                }
            }
        }
    }
