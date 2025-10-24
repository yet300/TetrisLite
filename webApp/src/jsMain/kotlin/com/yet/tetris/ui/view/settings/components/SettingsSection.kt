package com.yet.tetris.ui.view.settings.components

import mui.material.Box
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.PropsWithChildren
import web.cssom.integer
import web.cssom.rem

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
