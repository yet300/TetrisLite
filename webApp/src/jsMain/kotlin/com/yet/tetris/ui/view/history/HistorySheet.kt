package com.yet.tetris.ui.view.history

import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.feature.history.DateFilter
import com.yet.tetris.feature.history.HistoryComponent
import com.yet.tetris.ui.strings.Strings
import com.yet.tetris.ui.view.history.components.EmptyHistoryState
import com.yet.tetris.ui.view.history.components.GameRecordItem
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.useAsState
import mui.icons.material.Close
import mui.icons.material.FilterList
import mui.material.Box
import mui.material.CircularProgress
import mui.material.IconButton
import mui.material.List
import mui.material.Menu
import mui.material.MenuItem
import mui.material.Stack
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useRef
import react.useState
import web.cssom.AlignItems
import web.cssom.BorderBottom
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.JustifyContent
import web.cssom.Position
import web.cssom.number
import web.cssom.pct
import web.cssom.rem
import web.html.HTMLElement

val HistorySheet =
    FC<RProps<HistoryComponent>> { props ->
        val model by props.component.model.useAsState()
        val (filterMenuAnchor, setFilterMenuAnchor) = useState<HTMLElement?>(null)
        val filterButtonRef = useRef<HTMLElement>()

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
                div {
                    ref = filterButtonRef
                    IconButton {
                        onClick = { setFilterMenuAnchor(filterButtonRef.current) }
                        FilterList()
                    }
                }

                Typography {
                    variant = TypographyVariant.h5
                    +Strings.GAME_HISTORY
                }

                // Close button
                IconButton {
                    onClick = { props.component.onDismiss() }
                    Close()
                }

                // Filter Menu - anchored to button
                Menu {
                    anchorEl = filterMenuAnchor?.asDynamic()
                    open = filterMenuAnchor != null
                    onClose = { setFilterMenuAnchor(null) }

                    DateFilter.entries.forEach { filter ->
                        MenuItem {
                            onClick = {
                                props.component.onFilterChanged(filter)
                                setFilterMenuAnchor(null)
                            }
                            +filter.name
                                .replace("_", " ")
                                .lowercase()
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
                            EmptyHistoryState()
                        } else {
                            GamesList {
                                games = content.games
                                onDeleteGame = props.component::onDeleteGame
                            }
                        }
                    }
                }
            }
        }
    }

external interface GamesListProps : Props {
    var games: List<GameRecord>
    var onDeleteGame: (String) -> Unit
}

val GamesList =
    FC<GamesListProps> { props ->
        Stack {
            spacing = responsive(2)

            List {
                props.games.forEach { game ->
                    GameRecordItem {
                        this.game = game
                        this.onDelete = { props.onDeleteGame(game.id) }
                    }
                }
            }
        }
    }
