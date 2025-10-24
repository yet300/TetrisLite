package com.yet.tetris.ui.view.history.components

import com.yet.tetris.ui.strings.Strings
import mui.icons.material.SportsEsports
import mui.material.Box
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import web.cssom.AlignItems
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.JustifyContent
import web.cssom.pct
import web.cssom.rem

val EmptyHistoryState =
    FC {
        Box {
            sx {
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                justifyContent = JustifyContent.center
                height = 100.pct
                gap = 1.rem
            }

            SportsEsports {
                sx {
                    fontSize = 4.rem
                    color = Color("rgba(0, 0, 0, 0.3)")
                }
            }

            Typography {
                variant = TypographyVariant.h6
                sx { color = Color("rgba(0, 0, 0, 0.5)") }
                +Strings.NO_GAMES_YET
            }

            Typography {
                variant = TypographyVariant.body2
                sx { color = Color("rgba(0, 0, 0, 0.4)") }
                +Strings.START_GAME_PROMPT
            }
        }
    }
