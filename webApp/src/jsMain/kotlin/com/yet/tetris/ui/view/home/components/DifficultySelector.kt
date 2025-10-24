package com.yet.tetris.ui.view.home.components

import com.yet.tetris.domain.model.game.Difficulty
import mui.material.Box
import mui.material.ToggleButton
import mui.material.ToggleButtonGroup
import mui.system.sx
import react.FC
import react.Props
import web.cssom.BackdropFilter
import web.cssom.Border
import web.cssom.Color
import web.cssom.Display
import web.cssom.JustifyContent
import web.cssom.TextTransform
import web.cssom.integer
import web.cssom.pct
import web.cssom.px
import web.cssom.rem

external interface DifficultySelectorProps : Props {
    var selectedDifficulty: Difficulty
    var onDifficultyChanged: (Difficulty) -> Unit
}

@OptIn(ExperimentalWasmJsInterop::class)
val DifficultySelector = FC<DifficultySelectorProps> { props ->
    Box {
        sx {
            width = 100.pct
            maxWidth = 500.px
            display = Display.flex
            justifyContent = JustifyContent.center
            paddingTop = 2.rem
        }

        ToggleButtonGroup {
            value = props.selectedDifficulty
            exclusive = true
            fullWidth = true
            onChange = { _, newValue ->
                if (newValue != null) {
                    props.onDifficultyChanged(newValue.unsafeCast<Difficulty>())
                }
            }
            sx {
                backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                border = "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                borderRadius = 1.rem
                padding = 0.5.rem
            }

            Difficulty.entries.forEach { diff ->
                ToggleButton {
                    value = diff
                    sx {
                        color = Color("rgba(255, 255, 255, 0.9)")
                        borderRadius = 0.75.rem
                        border = "none".unsafeCast<Border>()
                        fontWeight = integer(600)
                        textTransform = "capitalize".unsafeCast<TextTransform>()

                        "&.Mui-selected" {
                            backgroundColor = Color("rgba(255, 255, 255, 0.25)")
                            color = Color("white")
                            hover {
                                backgroundColor = Color("rgba(255, 255, 255, 0.3)")
                            }
                        }

                        hover {
                            backgroundColor = Color("rgba(255, 255, 255, 0.15)")
                        }
                    }
                    +diff.name.lowercase().replaceFirstChar { it.uppercase() }
                }
            }
        }
    }
}
