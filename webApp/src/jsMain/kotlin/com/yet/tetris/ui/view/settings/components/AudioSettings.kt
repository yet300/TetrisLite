package com.yet.tetris.ui.view.settings.components

import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.ui.strings.Strings
import mui.material.Box
import mui.material.Button
import mui.material.ButtonGroup
import mui.material.ButtonGroupVariant
import mui.material.ButtonVariant
import mui.material.Size
import mui.material.Slider
import mui.material.Stack
import mui.material.Switch
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.create
import react.dom.html.ReactHTML.div
import web.cssom.AlignItems
import web.cssom.Display
import web.cssom.JustifyContent
import web.cssom.rem

external interface AudioSettingsProps : Props {
    var audioSettings: AudioSettings
    var onMusicToggled: (Boolean) -> Unit
    var onMusicVolumeChanged: (Float) -> Unit
    var onMusicThemeChanged: (MusicTheme) -> Unit
    var onSoundEffectsToggled: (Boolean) -> Unit
    var onSFXVolumeChanged: (Float) -> Unit
}

val AudioSettingsSection = FC<AudioSettingsProps> { props ->
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
                    checked = props.audioSettings.musicEnabled
                    onChange = { _, checked ->
                        props.onMusicToggled(checked)
                    }
                }
            }

            // Music Volume
            if (props.audioSettings.musicEnabled) {
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
                            +"${(props.audioSettings.musicVolume * 100).toInt()}%"
                        }
                    }

                    Slider {
                        value = props.audioSettings.musicVolume.toDouble()
                        onChange = { _, value, _ ->
                            val numberValue = value as Number
                            props.onMusicVolumeChanged(numberValue.toFloat())
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
                                variant = if (props.audioSettings.selectedMusicTheme == theme) {
                                    ButtonVariant.contained
                                } else {
                                    ButtonVariant.outlined
                                }
                                onClick = { props.onMusicThemeChanged(theme) }
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
                    checked = props.audioSettings.soundEffectsEnabled
                    onChange = { _, checked ->
                        props.onSoundEffectsToggled(checked)
                    }
                }
            }

            // SFX Volume
            if (props.audioSettings.soundEffectsEnabled) {
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
                            +"${(props.audioSettings.sfxVolume * 100).toInt()}%"
                        }
                    }

                    Slider {
                        value = props.audioSettings.sfxVolume.toDouble()
                        onChange = { _, value, _ ->
                            val numberValue = value as Number
                            props.onSFXVolumeChanged(numberValue.toFloat())
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
