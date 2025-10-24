package com.yet.tetris.ui.view.home

import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.feature.home.HomeComponent
import com.yet.tetris.ui.components.AppBarConfig
import com.yet.tetris.ui.components.Scaffold
import com.yet.tetris.ui.theme.AppColors
import com.yet.tetris.ui.view.history.HistorySheet
import com.yet.tetris.ui.view.settings.SettingsSheet
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.useAsState
import js.objects.unsafeJso
import mui.icons.material.History
import mui.icons.material.PlayArrow
import mui.icons.material.Replay
import mui.icons.material.Settings
import mui.material.Box
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.CircularProgress
import mui.material.Container
import mui.material.Drawer
import mui.material.DrawerAnchor
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.Size
import mui.material.ToggleButton
import mui.material.ToggleButtonGroup
import mui.system.sx
import react.FC
import react.create
import react.dom.html.ReactHTML.div
import web.cssom.AlignItems
import web.cssom.AutoLengthProperty
import web.cssom.BackdropFilter
import web.cssom.BackgroundImage
import web.cssom.Border
import web.cssom.BoxSizing
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.JustifyContent
import web.cssom.integer
import web.cssom.pct
import web.cssom.px
import web.cssom.rem
import web.cssom.vh

@OptIn(ExperimentalWasmJsInterop::class)
val HomeContent = FC<RProps<HomeComponent>> { props ->
    val model by props.component.model.useAsState()
    val bottomSheetSlot by props.component.childBottomSheetNavigation.useAsState()
    val activeSheet = bottomSheetSlot.child?.instance

    Scaffold {
        appBar = AppBarConfig(
            title = "TetrisLite",
            navigationIcon = {
                IconButton {
                    sx {
                        backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                        backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                        hover {
                            backgroundColor = Color("rgba(255, 255, 255, 0.2)")
                        }
                    }
                    color = IconButtonColor.inherit
                    onClick = { props.component.onOpenHistory() }
                    History()
                }
            },
            actions = {
                IconButton {
                    sx {
                        backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                        backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                        hover {
                            backgroundColor = Color("rgba(255, 255, 255, 0.2)")
                        }
                    }
                    color = IconButtonColor.inherit
                    onClick = { props.component.onOpenSettings() }
                    Settings()
                }
            }
        )

        sx {
            backgroundImage = AppColors.gradientBackground().unsafeCast<BackgroundImage>()
        }

        when (model) {
            is HomeComponent.Model.Loading -> {
                Box {
                    sx {
                        display = Display.flex
                        alignItems = AlignItems.center
                        justifyContent = JustifyContent.center
                        height = 100.vh
                    }
                    CircularProgress {
                        sx { color = Color("white") }
                    }
                }
            }

            is HomeComponent.Model.Content -> {
                val content = model as HomeComponent.Model.Content

                Container {
                    maxWidth = "sm"
                    sx {
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        alignItems = AlignItems.center
                        justifyContent = JustifyContent.spaceBetween
                        minHeight = "calc(100vh - 64px)".unsafeCast<web.cssom.MinHeight>()
                        padding = 1.5.rem
                        boxSizing = BoxSizing.borderBox
                    }

                    // Difficulty selector at top
                    Box {
                        sx {
                            width = 100.pct
                            maxWidth = 500.px
                            display = Display.flex
                            justifyContent = JustifyContent.center
                            paddingTop = 2.rem
                        }

                        ToggleButtonGroup {
                            value = content.settings.difficulty
                            exclusive = true
                            fullWidth = true
                            onChange = { _, newValue ->
                                if (newValue != null) {
                                    props.component.onDifficultyChanged(newValue.unsafeCast<Difficulty>())
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
                                        textTransform =
                                            "capitalize".unsafeCast<web.cssom.TextTransform>()

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

                    // Spacer
                    div { }

                    // Action buttons at bottom
                    Box {
                        sx {
                            width = 100.pct
                            maxWidth = 400.px
                            display = Display.flex
                            flexDirection = FlexDirection.column
                            gap = 1.rem
                            paddingBottom = 2.rem
                        }

                        // Start New Game button
                        Button {
                            variant = ButtonVariant.contained
                            size = Size.large
                            fullWidth = true
                            onClick = { props.component.onStartNewGame() }
                            startIcon = PlayArrow.create()
                            sx {
                                height = 56.px
                                backgroundColor = Color("rgba(255, 255, 255, 0.15)")
                                backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                border = "1px solid rgba(255, 255, 255, 0.3)".unsafeCast<Border>()
                                borderRadius = 1.rem
                                color = Color("white")
                                fontWeight = integer(700)
                                fontSize = 1.rem
                                textTransform = "none".unsafeCast<web.cssom.TextTransform>()
                                boxShadow =
                                    "0 8px 32px rgba(0, 0, 0, 0.1)".unsafeCast<web.cssom.BoxShadow>()

                                hover {
                                    backgroundColor = Color("rgba(255, 255, 255, 0.25)")
                                    boxShadow =
                                        "0 12px 40px rgba(0, 0, 0, 0.15)".unsafeCast<web.cssom.BoxShadow>()
                                }
                            }
                            +"Start New Game"
                        }

                        // Resume Game button (if available)
                        if (content.hasSavedGame) {
                            Button {
                                variant = ButtonVariant.outlined
                                size = Size.large
                                fullWidth = true
                                onClick = { props.component.onResumeGame() }
                                startIcon = Replay.create()
                                sx {
                                    height = 56.px
                                    backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                    backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                    border =
                                        "2px solid rgba(255, 255, 255, 0.3)".unsafeCast<Border>()
                                    borderRadius = 1.rem
                                    color = Color("white")
                                    fontWeight = integer(600)
                                    fontSize = 1.rem
                                    textTransform = "none".unsafeCast<web.cssom.TextTransform>()

                                    hover {
                                        backgroundColor = Color("rgba(255, 255, 255, 0.2)")
                                        border =
                                            "2px solid rgba(255, 255, 255, 0.4)".unsafeCast<Border>()
                                    }
                                }
                                +"Resume Game"
                            }
                        }
                    }
                }
            }
        }
    }

    // Bottom sheet drawer
    Drawer {
        anchor = DrawerAnchor.bottom
        open = activeSheet != null
        onClose = { _, _ -> props.component.onDismissBottomSheet() }

        PaperProps = unsafeJso {
            sx {
                borderTopLeftRadius = 16.px
                borderTopRightRadius = 16.px

                maxHeight = 90.vh

                maxWidth = 600.px

                marginLeft = "auto".unsafeCast<AutoLengthProperty>()
                marginRight = "auto".unsafeCast<AutoLengthProperty>()
            }
        }

        activeSheet?.let { child ->
            when (child) {
                is HomeComponent.BottomSheetChild.HistoryChild -> {
                    HistorySheet {
                        component = child.component
                    }
                }

                is HomeComponent.BottomSheetChild.SettingsChild -> {
                    SettingsSheet {
                        component = child.component
                    }
                }
            }
        }
    }
}
