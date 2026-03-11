package com.yet.tetris.ui.view.settings

import com.yet.tetris.feature.settings.SettingsComponent
import com.yet.tetris.ui.strings.Strings
import com.yet.tetris.ui.view.settings.components.AudioSettingsSection
import com.yet.tetris.ui.view.settings.components.ThemeSelector
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.useAsState
import mui.icons.material.Close
import mui.material.Box
import mui.material.IconButton
import mui.material.Stack
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import react.FC
import web.cssom.AlignItems
import web.cssom.BorderBottom
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.JustifyContent
import web.cssom.Position
import web.cssom.number
import web.cssom.pct
import web.cssom.rem

val SettingsSheet =
    FC<RProps<SettingsComponent>> { props ->
        val model by props.component.model.useAsState()
        val settings = model.settings

        Box {
            sx {
                display = Display.flex
                flexDirection = FlexDirection.column
                height = 100.pct
                position = Position.relative
            }

            // Header with title and close button
            Box {
                sx {
                    display = Display.flex
                    alignItems = AlignItems.center
                    justifyContent = JustifyContent.spaceBetween
                    padding = 1.5.rem
                    borderBottom = "1px solid rgba(0, 0, 0, 0.1)".unsafeCast<BorderBottom>()
                    backgroundColor = Color("white")
                }

                Typography {
                    variant = TypographyVariant.h5
                    +Strings.GAME_SETTINGS
                }

                IconButton {
                    onClick = { props.component.onClose() }
                    Close()
                }
            }

            // Content - Scrollable
            Box {
                sx {
                    flexGrow = number(1.0)
                    padding = 2.rem
                }

                Stack {
                    spacing = responsive(4)

                    // Theme Settings
                    ThemeSelector {
                        visualTheme = settings.themeConfig.visualTheme
                        pieceStyle = settings.themeConfig.pieceStyle
                        onVisualThemeChanged = props.component::onVisualThemeChanged
                        onPieceStyleChanged = props.component::onPieceStyleChanged
                    }

                    // Audio Settings
                    AudioSettingsSection {
                        audioSettings = settings.audioSettings
                        onMusicToggled = props.component::onMusicToggled
                        onMusicVolumeChanged = props.component::onMusicVolumeChanged
                        onMusicThemeChanged = props.component::onMusicThemeChanged
                        onSoundEffectsToggled = props.component::onSoundEffectsToggled
                        onSFXVolumeChanged = props.component::onSFXVolumeChanged
                    }
                }
            }
        }
    }
