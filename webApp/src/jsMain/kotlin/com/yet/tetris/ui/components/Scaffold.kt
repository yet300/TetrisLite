package com.yet.tetris.ui.components

import mui.icons.material.ArrowBack
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
import web.cssom.AlignItems
import web.cssom.BoxSizing
import web.cssom.Color
import web.cssom.Display
import web.cssom.Flex
import web.cssom.FlexBasis
import web.cssom.FlexDirection
import web.cssom.FontWeight
import web.cssom.JustifyContent
import web.cssom.None
import web.cssom.Overflow
import web.cssom.TextAlign
import web.cssom.number
import web.cssom.pct
import web.cssom.px
import web.cssom.rem
import web.cssom.vh

external interface ScaffoldProps : PropsWithChildren, PropsWithSx {
    var appBar: AppBarConfig?
}

data class AppBarConfig(
    val title: String,
    val onBackClick: (() -> Unit)? = null,
    val navigationIcon: (ChildrenBuilder.() -> Unit)? = null,
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
            backgroundColor = com.yet.tetris.ui.theme.AppColors.Dark.background
            +props.sx
        }

        props.appBar?.also { appBar ->
            AppBar {
                position = AppBarPosition.static
                sx {
                    backgroundColor = Color("transparent")
                    backgroundImage = None.none
                    boxShadow = None.none
                }

                Toolbar {
                    sx {
                        display = Display.flex
                        justifyContent = JustifyContent.spaceBetween
                        alignItems = AlignItems.center
                        minHeight = 64.px
                    }

                    // Left side - navigation icon or back button
                    Box {
                        sx {
                            display = Display.flex
                            alignItems = AlignItems.center
                            minWidth = 48.px
                        }

                        if (appBar.navigationIcon != null) {
                            appBar.navigationIcon.invoke(this)
                        } else if (appBar.onBackClick != null) {
                            IconButton {
                                size = Size.large
                                edge = IconButtonEdge.start
                                color = IconButtonColor.inherit
                                onClick = { appBar.onBackClick.invoke() }
                                ArrowBack()
                            }
                        }
                    }

                    // Center - title
                    Typography {
                        sx {
                            flexGrow = number(1.0)
                            textAlign = TextAlign.center
                            fontWeight = FontWeight.bold
                            color = Color("rgba(255, 255, 255, 0.95)")
                        }
                        variant = TypographyVariant.h6
                        +appBar.title
                    }

                    // Right side - actions
                    Box {
                        sx {
                            display = Display.flex
                            alignItems = AlignItems.center
                            gap = 0.5.rem
                            minWidth = 48.px
                        }
                        appBar.actions?.invoke(this)
                    }
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
