package com.yet.tetris.ui.view.game.components

import mui.material.Box
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import web.cssom.AlignItems
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.TextTransform
import web.cssom.WhiteSpace
import web.cssom.integer
import web.cssom.px
import web.cssom.rem

external interface StatItemProps : Props {
    var label: String
    var value: String
}

val StatItem =
    FC<StatItemProps> { props ->
        Box {
            sx {
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                minWidth = 0.px
            }

            Typography {
                variant = TypographyVariant.caption
                sx {
                    color = Color("rgba(255, 255, 255, 0.7)")
                    textTransform = TextTransform.uppercase
                    fontSize = 0.7.rem
                    whiteSpace = WhiteSpace.nowrap
                }
                +props.label
            }

            Typography {
                variant = TypographyVariant.h6
                sx {
                    fontWeight = integer(700)
                    color = Color("#39FF14") // Terminal green
                    fontSize = 1.1.rem
                    whiteSpace = WhiteSpace.nowrap
                }
                +props.value
            }
        }
    }
