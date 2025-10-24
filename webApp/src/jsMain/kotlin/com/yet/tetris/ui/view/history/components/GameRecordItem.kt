package com.yet.tetris.ui.view.history.components

import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.ui.strings.Strings
import com.yet.tetris.utils.formatDate
import mui.icons.material.Delete
import mui.icons.material.SportsEsports
import mui.material.Box
import mui.material.Card
import mui.material.CardContent
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.useState
import web.cssom.AlignItems
import web.cssom.Color
import web.cssom.Cursor
import web.cssom.Display
import web.cssom.JustifyContent
import web.cssom.Overflow
import web.cssom.Position
import web.cssom.Transform
import web.cssom.Transition
import web.cssom.integer
import web.cssom.px
import web.cssom.rem

external interface GameRecordItemProps : Props {
    var game: GameRecord
    var onDelete: () -> Unit
}

val GameRecordItem =
    FC<GameRecordItemProps> { props ->
        val (startX, setStartX) = useState<Double?>(null)
        val (offsetX, setOffsetX) = useState(0.0)
        val (isSwiping, setIsSwiping) = useState(false)

        Card {
            elevation = 2
            sx {
                borderRadius = 1.5.rem
                backgroundColor = Color("rgba(102, 126, 234, 0.05)")
                position = Position.relative
                overflow = "hidden".unsafeCast<Overflow>()
                cursor = if (isSwiping) Cursor.grabbing else Cursor.grab
                transform = "translateX(${offsetX}px)".unsafeCast<Transform>()
                transition =
                    (if (isSwiping) "none" else "transform 0.3s ease-out").unsafeCast<Transition>()
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
                            setStartX(event.touches[0]!!.clientX)
                            setIsSwiping(true)
                        }
                    }

                    this@CardContent.onTouchMove = { event ->
                        startX?.let { start ->
                            if (event.touches.length > 0) {
                                val currentX = event.touches[0]!!.clientX
                                val diff = currentX - start
                                if (diff < 0) {
                                    setOffsetX(diff.coerceAtLeast(-100.0))
                                }
                            }
                        }
                    }

                    this@CardContent.onTouchEnd = {
                        setIsSwiping(false)
                        if (offsetX < -60) {
                            props.onDelete()
                        } else {
                            setOffsetX(0.0)
                        }
                        setStartX(null)
                    }

                    // Mouse event handlers
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
                            +Strings.linesLabel(props.game.linesCleared.toLong())
                        }
                    }
                }
            }
        }
    }
