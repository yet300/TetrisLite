package com.yet.tetris.ui.view.history

import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.feature.history.DateFilter
import com.yet.tetris.feature.history.HistoryComponent
import com.yet.tetris.ui.strings.Strings
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
import web.cssom.Cursor
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.JustifyContent
import web.cssom.Position
import web.cssom.integer
import web.cssom.number
import web.cssom.pct
import web.cssom.px
import web.cssom.rem
import web.html.HTMLElement

val HistorySheet = FC<RProps<HistoryComponent>> { props ->
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
                +Strings.gameHistory
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

val EmptyHistoryState = FC {
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
            +Strings.noGamesYet
        }

        Typography {
            variant = TypographyVariant.body2
            sx { color = Color("rgba(0, 0, 0, 0.4)") }
            +Strings.startGamePrompt
        }
    }
}

external interface GamesListProps : Props {
    var games: List<GameRecord>
    var onDeleteGame: (String) -> Unit
}

val GamesList = FC<GamesListProps> { props ->
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

external interface GameRecordItemProps : Props {
    var game: GameRecord
    var onDelete: () -> Unit
}

val GameRecordItem = FC<GameRecordItemProps> { props ->
    val (startX, setStartX) = useState<Double?>(null)
    val (offsetX, setOffsetX) = useState(0.0)
    val (isSwiping, setIsSwiping) = useState(false)

    Card {
        elevation = 2
        sx {
            borderRadius = 1.5.rem
            backgroundColor = Color("rgba(102, 126, 234, 0.05)")
            position = Position.relative
            overflow = "hidden".unsafeCast<web.cssom.Overflow>()
            cursor = if (isSwiping) Cursor.grabbing else Cursor.grab
            transform = "translateX(${offsetX}px)".unsafeCast<web.cssom.Transform>()
            transition =
                (if (isSwiping) "none" else "transform 0.3s ease-out").unsafeCast<web.cssom.Transition>()
        }

        // Delete background (revealed when swiping)
        if (offsetX < -10) {
            Box {
                sx {
                    position = Position.absolute
                    top = 0.px
                    right = 0.px
                    bottom = 0.px
                    width = 80.px
                    backgroundColor = Color("rgba(244, 67, 54, 0.9)")
                    display = Display.flex
                    alignItems = AlignItems.center
                    justifyContent = JustifyContent.center
                }

                Delete {
                    sx {
                        color = Color("white")
                        fontSize = 1.5.rem
                    }
                }
            }
        }

        CardContent {
            sx {
                // Touch event handlers
                this@CardContent.onTouchStart = { event ->
                    if (event.touches.length > 0) {
                        setStartX(event.touches[0].clientX)
                        setIsSwiping(true)
                    }
                }

                this@CardContent.onTouchMove = { event ->
                    startX?.let { start ->
                        if (event.touches.length > 0) {
                            val currentX = event.touches[0].clientX
                            val diff = currentX - start
                            // Only allow left swipe
                            if (diff < 0) {
                                setOffsetX(diff.coerceAtLeast(-100.0))
                            }
                        }
                    }
                }

                this@CardContent.onTouchEnd = {
                    setIsSwiping(false)
                    if (offsetX < -60) {
                        // Swipe threshold reached - delete
                        props.onDelete()
                    } else {
                        // Snap back
                        setOffsetX(0.0)
                    }
                    setStartX(null)
                }

                // Mouse event handlers for desktop
                this@CardContent.onMouseDown = { event ->
                    setStartX(event.clientX)
                    setIsSwiping(true)
                }

                this@CardContent.onMouseMove = { event ->
                    if (isSwiping) {
                        startX?.let { start ->
                            val diff = event.clientX - start
                            if (diff < 0) {
                                setOffsetX(diff.coerceAtLeast(-100.0))
                            }
                        }
                    }
                }

                this@CardContent.onMouseUp = {
                    setIsSwiping(false)
                    if (offsetX < -60) {
                        props.onDelete()
                    } else {
                        setOffsetX(0.0)
                    }
                    setStartX(null)
                }

                this@CardContent.onMouseLeave = {
                    if (isSwiping) {
                        setIsSwiping(false)
                        setOffsetX(0.0)
                        setStartX(null)
                    }
                }
            }

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
                                color = Color("rgba(0, 0, 0, 0.6)")
                            }
                        }

                        Typography {
                            variant = TypographyVariant.caption
                            sx {
                                fontWeight = integer(600)
                                color = Color("rgba(0, 0, 0, 0.6)")
                            }
                            +props.game.difficulty.name
                        }

                        Typography {
                            variant = TypographyVariant.caption
                            sx {
                                color = Color("rgba(0, 0, 0, 0.5)")
                            }
                            +" • ${formatDate(props.game.timestamp)}"
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
                        +Strings.scoreLabel(props.game.score)
                    }

                    // Lines
                    Typography {
                        variant = TypographyVariant.body1
                        sx {
                            color = Color("rgba(0, 0, 0, 0.7)")
                        }
                        +Strings.linesLabel(props.game.linesCleared)
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

