package com.yet.tetris.ui.view.home

import com.yet.tetris.feature.home.HomeComponent
import com.yet.tetris.ui.components.AppBarConfig
import com.yet.tetris.ui.components.Scaffold
import com.yet.tetris.ui.strings.Strings
import com.yet.tetris.ui.theme.AppColors
import com.yet.tetris.ui.view.history.HistorySheet
import com.yet.tetris.ui.view.home.components.ActionButtons
import com.yet.tetris.ui.view.home.components.DifficultySelector
import com.yet.tetris.ui.view.settings.SettingsSheet
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.useAsState
import js.objects.unsafeJso
import mui.icons.material.History
import mui.icons.material.Settings
import mui.material.Box
import mui.material.CircularProgress
import mui.material.Container
import mui.material.Drawer
import mui.material.DrawerAnchor
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.system.sx
import react.FC
import web.cssom.AlignItems
import web.cssom.AutoLengthProperty
import web.cssom.BackdropFilter
import web.cssom.BackgroundImage
import web.cssom.BoxSizing
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.JustifyContent
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
            title = Strings.appTitle,
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
                    DifficultySelector {
                        selectedDifficulty = content.settings.difficulty
                        onDifficultyChanged = props.component::onDifficultyChanged
                    }

                    // Spacer
                    Box { }

                    // Action buttons at bottom
                    ActionButtons {
                        hasSavedGame = content.hasSavedGame
                        onStartNewGame = props.component::onStartNewGame
                        onResumeGame = props.component::onResumeGame
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
