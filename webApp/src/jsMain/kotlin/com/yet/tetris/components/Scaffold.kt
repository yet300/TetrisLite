package com.yet.tetris.components

import mui.material.AppBar
import mui.material.AppBarPosition
import mui.material.Box
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.IconButtonEdge
import mui.material.Size
import mui.material.Toolbar
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.PropsWithSx
import mui.system.sx
import react.ChildrenBuilder
import react.FC
import react.PropsWithChildren
import web.cssom.BackgroundImage
import web.cssom.BoxSizing
import web.cssom.Display
import web.cssom.Flex
import web.cssom.FlexBasis
import web.cssom.FlexDirection
import web.cssom.Overflow
import web.cssom.number
import web.cssom.pct
import web.cssom.vh

external interface ScaffoldProps : PropsWithChildren, PropsWithSx {
    var appBar: AppBarConfig?
}

data class AppBarConfig(
    val title: String,
    val onBackClick: (() -> Unit)? = null,
    val actions: (ChildrenBuilder.() -> Unit)? = null
)

val Scaffold: FC<ScaffoldProps> = FC { props ->
    Box {
        sx {
            display = Display.flex
            flexDirection = FlexDirection.column
            boxSizing = BoxSizing.borderBox
            height = 100.vh
            width = 100.pct
            +props.sx
        }

        props.appBar?.also { appBar ->
            AppBar {
                position = AppBarPosition.static
                sx {
                    backgroundImage =
                        "linear-gradient(135deg, rgb(102, 126, 234) 0%, rgb(118, 75, 162) 100%)".unsafeCast<BackgroundImage>()
                }

                Toolbar {
                    if (appBar.onBackClick != null) {
                        IconButton {
                            size = Size.large
                            edge = IconButtonEdge.start
                            color = IconButtonColor.inherit
                            onClick = { appBar.onBackClick.invoke() }

                            mui.icons.material.ArrowBack()
                        }
                    }

                    Typography {
                        sx {
                            flexGrow = number(1.0)
                        }
                        variant = TypographyVariant.h6
                        +appBar.title
                    }

                    appBar.actions?.invoke(this)
                }
            }
        }

        Box {
            sx {
                flex = Flex(
                    grow = number(1.0),
                    shrink = number(0.0),
                    basis = "auto".unsafeCast<FlexBasis>()
                )
                overflow = "auto".unsafeCast<Overflow>()
            }
            +props.children
        }
    }
}
