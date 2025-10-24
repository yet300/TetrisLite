package com.yet.tetris.ui.view.settings

import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.settings.KeyboardLayout
import com.yet.tetris.domain.model.settings.SwipeLayout
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.VisualTheme
import com.yet.tetris.feature.settings.SettingsComponent
import com.yet.tetris.ui.strings.Strings
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.useAsState
import mui.material.Box
import mui.material.Button
import mui.material.ButtonGroup
import mui.material.ButtonGroupVariant
import mui.material.ButtonVariant
import mui.material.Chip
import mui.material.ChipColor
import mui.material.ChipVariant
import mui.material.CircularProgress
import mui.material.Size
import mui.material.Slider
import mui.material.Stack
import mui.material.Switch
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import react.FC
import react.PropsWithChildren
import react.create
import react.dom.html.ReactHTML.div
import web.cssom.AlignItems
import web.cssom.BorderTop
import web.cssom.BoxShadow
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.FlexWrap
import web.cssom.JustifyContent
import web.cssom.Position
import web.cssom.integer
import web.cssom.number
import web.cssom.pct
import web.cssom.px
import web.cssom.rem

val SettingsSheet = FC<RProps<SettingsComponent>> { props ->
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
                    +Strings.gameSettings
                }

                SettingsSection {
                    title = Strings.visualTheme

                    Box {
                        sx {
                            display = Display.flex
                            flexWrap = FlexWrap.wrap
                            gap = 1.rem
                        }

                        VisualTheme.entries.forEach { theme ->
                            Chip {
                                label = div.create {
                                    +theme.name.lowercase().replaceFirstChar { it.uppercase() }
                                }
                                variant = if (settings.themeConfig.visualTheme == theme) {
                                    ChipVariant.filled
                                } else {
                                    ChipVariant.outlined
                                }
                                color = if (settings.themeConfig.visualTheme == theme) {
                                    ChipColor.primary
                                } else {
                                    ChipColor.default
                                }
                                onClick = { props.component.onVisualThemeChanged(theme) }
                            }
                        }
                    }
                }

                // Piece Style
                SettingsSection {
                    title = Strings.pieceStyle

                    Box {
                        sx {
                            display = Display.flex
                            flexWrap = FlexWrap.wrap
                            gap = 1.rem
                        }

                        PieceStyle.entries.forEach { style ->
                            Chip {
                                label = div.create {
                                    +style.name.lowercase().replaceFirstChar { it.uppercase() }
                                }
                                variant = if (settings.themeConfig.pieceStyle == style) {
                                    ChipVariant.filled
                                } else {
                                    ChipVariant.outlined
                                }
                                color = if (settings.themeConfig.pieceStyle == style) {
                                    ChipColor.primary
                                } else {
                                    ChipColor.default
                                }
                                onClick = { props.component.onPieceStyleChanged(style) }
                            }
                        }
                    }
                }

                // Keyboard Layout
                SettingsSection {
                    title = Strings.keyboardLayout

                    ButtonGroup {
                        fullWidth = true
                        variant = ButtonGroupVariant.outlined

                        KeyboardLayout.entries.forEach { layout ->
                            Button {
                                variant = if (settings.keyboardLayout == layout) {
                                    ButtonVariant.contained
                                } else {
                                    ButtonVariant.outlined
                                }
                                onClick = { props.component.onKeyboardLayoutChanged(layout) }
                                +layout.name
                            }
                        }
                    }
                }

                // Swipe Layout
                SettingsSection {
                    title = Strings.swipeLayout

                    ButtonGroup {
                        fullWidth = true
                        variant = ButtonGroupVariant.outlined

                        SwipeLayout.entries.forEach { layout ->
                            Button {
                                variant = if (settings.swipeLayout == layout) {
                                    ButtonVariant.contained
                                } else {
                                    ButtonVariant.outlined
                                }
                                onClick = { props.component.onSwipeLayoutChanged(layout) }
                                +layout.name
                            }
                        }
                    }
                }

                // Audio Settings
                SettingsSection {
                    title = Strings.audio

                    Stack {
                        spacing = responsive(4)


                        // Music Toggle
                        Box {
                            sx {
                                display = Display.flex
                                alignItems = AlignItems.center
                                justifyContent = JustifyContent.spaceBetween
                            }

                            Typography { +Strings.music }

                            Switch {
                                checked = settings.audioSettings.musicEnabled
                                onChange = { _, checked ->
                                    props.component.onMusicToggled(checked)
                                }
                            }
                        }

                        // Music Volume
                        if (settings.audioSettings.musicEnabled) {
                            Box {
                                Typography {
                                    variant = TypographyVariant.body2
                                    sx {
                                        display = Display.flex
                                        justifyContent = JustifyContent.spaceBetween
                                        marginBottom = 0.5.rem
                                    }
                                    div.create {
                                        +Strings.musicVolume
                                    }
                                    div.create {
                                        +"${(settings.audioSettings.musicVolume * 100).toInt()}%"
                                    }
                                }

                                Slider {
                                    value = settings.audioSettings.musicVolume.toDouble()
                                    onChange = { _, value, _ ->
                                        val numberValue = value as Number
                                        props.component.onMusicVolumeChanged(numberValue.toFloat())
                                    }
                                    min = 0.0
                                    max = 1.0
                                    step = 0.01
                                }
                            }

                            // Music Theme
                            Box {
                                Typography {
                                    variant = TypographyVariant.body2
                                    sx { marginBottom = 0.5.rem }
                                    +Strings.musicTheme
                                }

                                ButtonGroup {
                                    fullWidth = true
                                    variant = ButtonGroupVariant.outlined
                                    size = Size.small

                                    MusicTheme.entries.forEach { theme ->
                                        Button {
                                            variant =
                                                if (settings.audioSettings.selectedMusicTheme == theme) {
                                                    ButtonVariant.contained
                                                } else {
                                                    ButtonVariant.outlined
                                                }
                                            onClick =
                                                { props.component.onMusicThemeChanged(theme) }
                                            +theme.name.replace("_", " ")
                                        }
                                    }
                                }
                            }
                        }

                        // Sound Effects Toggle
                        Box {
                            sx {
                                display = Display.flex
                                alignItems = AlignItems.center
                                justifyContent = JustifyContent.spaceBetween
                            }

                            Typography { +Strings.soundEffects }

                            Switch {
                                checked = settings.audioSettings.soundEffectsEnabled
                                onChange = { _, checked ->
                                    props.component.onSoundEffectsToggled(checked)
                                }
                            }
                        }

                        // SFX Volume
                        if (settings.audioSettings.soundEffectsEnabled) {
                            Box {
                                Typography {
                                    variant = TypographyVariant.body2
                                    sx {
                                        display = Display.flex
                                        justifyContent = JustifyContent.spaceBetween
                                        marginBottom = 0.5.rem
                                    }
                                    div.create {
                                        +Strings.sfxVolume
                                    }
                                    div.create {
                                        +"${(settings.audioSettings.sfxVolume * 100).toInt()}%"
                                    }
                                }

                                Slider {
                                    value = settings.audioSettings.sfxVolume.toDouble()
                                    onChange = { _, value, _ ->
                                        val numberValue = value as Number
                                        props.component.onSFXVolumeChanged(value.toFloat())
                                    }
                                    min = 0.0
                                    max = 1.0
                                    step = 0.01
                                }
                            }
                        }
                    }
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
                +Strings.discard
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
                    +Strings.save
                }
            }
        }
    }
}

external interface SettingsSectionProps : PropsWithChildren {
    var title: String
}

val SettingsSection = FC<SettingsSectionProps> { props ->
    Box {
        sx {
            marginBottom = 2.rem
        }

        Typography {
            variant = TypographyVariant.subtitle1
            sx {
                marginBottom = 1.rem
                fontWeight = integer(600)
            }
            +props.title
        }

        +props.children
    }
}
