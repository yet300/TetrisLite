package com.yet.tetris.ui.view.history

import com.yet.tetris.feature.history.DateFilter
import com.yet.tetris.feature.history.HistoryComponent
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.useAsState
import mui.icons.material.Close
import mui.icons.material.Delete
import mui.icons.material.FilterList
import mui.icons.material.SportsEsports
import mui.material.Box
import mui.material.Card
import mui.material.CardContent
import mui.material.CircularProgress
import mui.material.IconButton
import mui.material.List
import mui.material.Menu
import mui.material.MenuItem
import mui.material.Size
import mui.material.Stack
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import react.FC
import react.useState
import web.cssom.AlignItems
import web.cssom.BorderBottom
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.JustifyContent
import web.cssom.Position
import web.cssom.integer
import web.cssom.number
import web.cssom.pct
import web.cssom.rem

val HistorySheet = FC<RProps<HistoryComponent>> { props ->
    val model by props.component.model.useAsState()
    val (showFilterMenu, setShowFilterMenu) = useState(false)

    Box {
        sx {
            display = Display.flex
            flexDirection = FlexDirection.column
            height = 100.pct
            position = Position.relative
        }

        // Header with Filter and Close buttons
        Box {
            sx {
                display = Display.flex
                alignItems = AlignItems.center
                justifyContent = JustifyContent.spaceBetween
                padding = 1.5.rem
                borderBottom = "1px solid rgba(0, 0, 0, 0.1)".unsafeCast<BorderBottom>()
                backgroundColor = Color("white")
            }

            // Filter button
            IconButton {
                onClick = { setShowFilterMenu(true) }
                FilterList()
            }

            Typography {
                variant = TypographyVariant.h5
                +"Game History"
            }

            // Close button
            IconButton {
                onClick = { props.component.onDismiss() }
                Close()
            }

            // Filter Menu
            Menu {
                open = showFilterMenu
                onClose = { setShowFilterMenu(false) }

                DateFilter.entries.forEach { filter ->
                    MenuItem {
                        onClick = {
                            props.component.onFilterChanged(filter)
                            setShowFilterMenu(false)
                        }
                        +filter.name.replace("_", " ").lowercase()
                            .replaceFirstChar { it.uppercase() }
                    }
                }
            }
        }

        // Content
        Box {
            sx {
                flexGrow = number(1.0)
                padding = 2.rem
            }

            when (model) {
                is HistoryComponent.Model.Loading -> {
                    Box {
                        sx {
                            display = Display.flex
                            alignItems = AlignItems.center
                            justifyContent = JustifyContent.center
                            height = 100.pct
                        }
                        CircularProgress()
                    }
                }

                is HistoryComponent.Model.Content -> {
                    val content = model as HistoryComponent.Model.Content

                    if (content.games.isEmpty()) {
                        // Empty state
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
                                +"No games yet"
                            }

                            Typography {
                                variant = TypographyVariant.body2
                                sx { color = Color("rgba(0, 0, 0, 0.4)") }
                                +"Start playing to see your history"
                            }
                        }
                    } else {
                        // Games list
                        Stack {
                            spacing = responsive(2)

                            List {
                                content.games.forEach { game ->
                                    Card {
                                        elevation = 2
                                        sx {
                                            borderRadius = 1.5.rem
                                            backgroundColor = Color("rgba(102, 126, 234, 0.05)")
                                        }

                                        CardContent {
                                            Box {
                                                sx {
                                                    display = Display.flex
                                                    justifyContent = JustifyContent.spaceBetween
                                                    alignItems = AlignItems.flexStart
                                                }

                                                // Game info
                                                Box {
                                                    // Difficulty and date
                                                    Box {
                                                        sx {
                                                            display = Display.flex
                                                            alignItems = AlignItems.center
                                                            gap = 0.5.rem
                                                            marginBottom = 0.5.rem
                                                        }

                                                        SportsEsports {
                                                            sx {
                                                                fontSize = 1.rem
                                                                color =
                                                                    Color("rgba(0, 0, 0, 0.6)")
                                                            }
                                                        }

                                                        Typography {
                                                            variant = TypographyVariant.caption
                                                            sx {
                                                                fontWeight = integer(600)
                                                                color =
                                                                    Color("rgba(0, 0, 0, 0.6)")
                                                            }
                                                            +game.difficulty.name
                                                        }

                                                        Typography {
                                                            variant = TypographyVariant.caption
                                                            sx {
                                                                color =
                                                                    Color("rgba(0, 0, 0, 0.5)")
                                                            }
                                                            +" • ${formatDate(game.timestamp)}"
                                                        }
                                                    }

                                                    // Score
                                                    Typography {
                                                        variant = TypographyVariant.h5
                                                        sx {
                                                            fontWeight = integer(600)
                                                            color = Color("#667eea")
                                                            marginBottom = 0.25.rem
                                                        }
                                                        +"Score: ${game.score}"
                                                    }

                                                    // Lines
                                                    Typography {
                                                        variant = TypographyVariant.body1
                                                        sx {
                                                            color = Color("rgba(0, 0, 0, 0.7)")
                                                        }
                                                        +"Lines: ${game.linesCleared}"
                                                    }
                                                }

                                                // Delete button
                                                IconButton {
                                                    size = Size.small
                                                    onClick =
                                                        { props.component.onDeleteGame(game.id) }
                                                    sx {
                                                        color = Color("rgba(244, 67, 54, 0.7)")
                                                        hover {
                                                            backgroundColor =
                                                                Color("rgba(244, 67, 54, 0.1)")
                                                        }
                                                    }
                                                    Delete()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = js("new Date(timestamp)")

    val month = (date.getMonth() + 1).toString().padStart(2, '0')
    val day = date.getDate().toString().padStart(2, '0')
    val hours = date.getHours().toString().padStart(2, '0')
    val minutes = date.getMinutes().toString().padStart(2, '0')
    return "$month/$day $hours:$minutes"
}

