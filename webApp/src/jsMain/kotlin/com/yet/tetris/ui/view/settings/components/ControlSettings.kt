package com.yet.tetris.ui.view.settings.components

import com.yet.tetris.domain.model.settings.KeyboardLayout
import com.yet.tetris.domain.model.settings.SwipeLayout
import com.yet.tetris.ui.strings.Strings
import mui.material.Button
import mui.material.ButtonGroup
import mui.material.ButtonGroupVariant
import mui.material.ButtonVariant
import mui.material.Stack
import mui.system.responsive
import react.FC
import react.Props

external interface ControlSettingsProps : Props {
    var keyboardLayout: KeyboardLayout
    var swipeLayout: SwipeLayout
    var onKeyboardLayoutChanged: (KeyboardLayout) -> Unit
    var onSwipeLayoutChanged: (SwipeLayout) -> Unit
}

val ControlSettings =
    FC<ControlSettingsProps> { props ->
        Stack {
            spacing = responsive(4)

            // Keyboard Layout
            SettingsSection {
                title = Strings.KEYBOARD_LAYOUT

                ButtonGroup {
                    fullWidth = true
                    variant = ButtonGroupVariant.outlined

                    KeyboardLayout.entries.forEach { layout ->
                        Button {
                            variant =
                                if (props.keyboardLayout == layout) {
                                    ButtonVariant.contained
                                } else {
                                    ButtonVariant.outlined
                                }
                            onClick = { props.onKeyboardLayoutChanged(layout) }
                            +layout.name
                        }
                    }
                }
            }

            // Swipe Layout
            SettingsSection {
                title = Strings.SWIPE_LAYOUT

                ButtonGroup {
                    fullWidth = true
                    variant = ButtonGroupVariant.outlined

                    SwipeLayout.entries.forEach { layout ->
                        Button {
                            variant =
                                if (props.swipeLayout == layout) {
                                    ButtonVariant.contained
                                } else {
                                    ButtonVariant.outlined
                                }
                            onClick = { props.onSwipeLayoutChanged(layout) }
                            +layout.name
                        }
                    }
                }
            }
        }
    }
