package com.yet.tetris.ui.view.home

import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.feature.home.HomeComponent
import com.yet.tetris.ui.components.AppBarConfig
import com.yet.tetris.ui.components.Scaffold
import com.yet.tetris.ui.view.history.HistorySheet
import com.yet.tetris.ui.view.settings.SettingsSheet
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.useAsState
import mui.icons.material.History
import mui.icons.material.PlayArrow
import mui.icons.material.Replay
import mui.icons.material.Settings
import mui.material.Box
import mui.material.Button
import mui.material.ButtonColor
import mui.material.ButtonVariant
import mui.material.CircularProgress
import mui.material.Container
import mui.material.Drawer
import mui.material.DrawerAnchor
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.Paper
import mui.material.Size
import mui.material.Stack
import mui.material.ToggleButton
import mui.material.ToggleButtonGroup
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import react.FC
import react.create
import web.cssom.AlignItems
import web.cssom.BackgroundImage
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.FontWeight
import web.cssom.JustifyContent
import web.cssom.pct
import web.cssom.px
import web.cssom.rem
import web.cssom.vw

val HomeContent = FC<RProps<HomeComponent>> { props ->
    val model by props.component.model.useAsState()
    val bottomSheetSlot by props.component.childBottomSheetNavigation.useAsState()
    val activeSheet = bottomSheetSlot.child?.instance

    Scaffold {
        appBar = AppBarConfig(
            title = "TetrisLite",
            actions = {
                IconButton {
                    color = IconButtonColor.inherit
                    onClick = { props.component.onOpenHistory() }
                    History()
                }
                IconButton {
                    color = IconButtonColor.inherit
                    onClick = { props.component.onOpenSettings() }
                    Settings()
                }
            }
        )

        sx {
            backgroundImage =
                "linear-gradient(135deg, rgb(102, 126, 234) 0%, rgb(118, 75, 162) 100%)".unsafeCast<BackgroundImage>()
        }

        when (model) {
            is HomeComponent.Model.Loading -> {
                Box {
                    sx {
                        display = Display.flex
                        alignItems = AlignItems.center
                        justifyContent = JustifyContent.center
                        height = 100.pct
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
                        justifyContent = JustifyContent.center
                        minHeight = 100.pct
                        padding = 3.rem
                        gap = 3.rem
                    }

                    // Difficulty selector
                    Paper {
                        elevation = 3
                        sx {
                            padding = 2.rem
                            width = 100.pct
                            backgroundColor = Color("rgba(255, 255, 255, 0.95)")
                            borderRadius = 2.rem
                        }

                        Typography {
                            variant = TypographyVariant.h6
                            sx {
                                marginBottom = 1.rem
                                color = Color("#667eea")
                            }
                            +"Difficulty"
                        }

                        ToggleButtonGroup {
                            value = content.settings.difficulty
                            exclusive = true
                            onChange = { _, newValue ->
                                props.component.onDifficultyChanged(newValue.unsafeCast<Difficulty>())
                            }

                            Difficulty.entries.forEach { diff ->
                                ToggleButton {
                                    value = diff
                                    +diff.name.lowercase().replaceFirstChar { it.uppercase() }
                                }
                            }
                        }
                    }

                    // Action buttons
                    Stack {
                        spacing = responsive(2)
                        sx {
                            width = 100.pct
                        }

                        Button {
                            variant = ButtonVariant.contained
                            size = Size.large
                            fullWidth = true
                            onClick = { props.component.onStartNewGame() }
                            sx {
                                padding = 1.rem
                                borderRadius = 1.rem
                                fontWeight = FontWeight.bold
                            }
                            startIcon = PlayArrow.create()
                            +"Start New Game"
                        }

                        if (content.hasSavedGame) {
                            Button {
                                variant = ButtonVariant.outlined
                                size = Size.large
                                fullWidth = true
                                color = ButtonColor.inherit
                                onClick = { props.component.onResumeGame() }
                                sx {
                                    padding = 1.rem
                                    borderRadius = 1.rem
                                }
                                startIcon = Replay.create()
                                +"Resume Game"
                            }
                        }
                    }

                }
            }
        }
    }


    Drawer {
        anchor = DrawerAnchor.bottom
        open = activeSheet != null
        onClose = { _, _ -> props.component.onDismissBottomSheet() }

        Box {
            sx {
                width = 600.px
                maxWidth = 100.vw
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

}
