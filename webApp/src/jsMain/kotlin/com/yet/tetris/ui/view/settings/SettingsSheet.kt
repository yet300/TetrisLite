package com.yet.tetris.ui.view.settings

import com.yet.tetris.feature.settings.SettingsComponent
import com.yet.tetris.ui.strings.Strings
import com.yet.tetris.ui.view.settings.components.AudioSettingsSection
import com.yet.tetris.ui.view.settings.components.ControlSettings
import com.yet.tetris.ui.view.settings.components.ThemeSelector
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.useAsState
import mui.material.Box
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.CircularProgress
import mui.material.Stack
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import react.FC
import web.cssom.BorderTop
import web.cssom.BoxShadow
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.Position
import web.cssom.integer
import web.cssom.number
import web.cssom.pct
import web.cssom.px
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

            // Content - Scrollable
            Box {
                sx {
                    flexGrow = number(1.0)
                    padding = 2.rem
                    paddingBottom = 6.rem // Space for buttons at bottom
                }

                Stack {
                    spacing = responsive(4)

                    // Title
                    Typography {
                        variant = TypographyVariant.h5
                        sx {
                            fontWeight = integer(700)
                            marginBottom = 1.rem
                        }
                        +Strings.GAME_SETTINGS
                    }

                    // Theme Settings
                    ThemeSelector {
                        visualTheme = settings.themeConfig.visualTheme
                        pieceStyle = settings.themeConfig.pieceStyle
                        onVisualThemeChanged = props.component::onVisualThemeChanged
                        onPieceStyleChanged = props.component::onPieceStyleChanged
                    }

                    // Control Settings
                    ControlSettings {
                        keyboardLayout = settings.keyboardLayout
                        swipeLayout = settings.swipeLayout
                        onKeyboardLayoutChanged = props.component::onKeyboardLayoutChanged
                        onSwipeLayoutChanged = props.component::onSwipeLayoutChanged
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

            // Action Buttons - Fixed at bottom
            Box {
                sx {
                    position = Position.absolute
                    bottom = 0.px
                    left = 0.px
                    right = 0.px
                    display = Display.flex
                    gap = 1.rem
                    padding = 1.5.rem
                    backgroundColor = Color("white")
                    borderTop = "1px solid rgba(0, 0, 0, 0.1)".unsafeCast<BorderTop>()
                    boxShadow = "0 -2px 10px rgba(0, 0, 0, 0.1)".unsafeCast<BoxShadow>()
                }

                Button {
                    variant = ButtonVariant.outlined
                    fullWidth = true
                    disabled = !model.hasUnsavedChanges
                    onClick = { props.component.onDiscard() }
                    +Strings.DISCARD
                }

                Button {
                    variant = ButtonVariant.contained
                    fullWidth = true
                    disabled = !model.hasUnsavedChanges || model.isSaving
                    onClick = { props.component.onSave() }

                    if (model.isSaving) {
                        CircularProgress {
                            size = 20
                            sx { color = Color("white") }
                        }
                    } else {
                        +Strings.SAVE
                    }
                }
            }
        }
    }
