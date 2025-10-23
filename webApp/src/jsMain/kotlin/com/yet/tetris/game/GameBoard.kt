package com.yet.tetris.game

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import mui.material.Box
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import web.cssom.AspectRatio
import web.cssom.BoxShadow
import web.cssom.Color
import web.cssom.Display
import web.cssom.GridTemplateColumns
import web.cssom.GridTemplateRows
import web.cssom.px
import web.cssom.vh

external interface GameBoardProps : Props {
    var gameState: GameState
    var ghostY: Int?
}

val GameBoard = FC<GameBoardProps> { props ->
    val board = props.gameState.board
    val currentPiece = props.gameState.currentPiece
    val boardRows = board.height
    val boardCols = board.width

    Box {
        sx {
            display = Display.grid
            gridTemplateRows = "repeat($boardRows, 1fr)".unsafeCast<GridTemplateRows>()
            gridTemplateColumns = "repeat($boardCols, 1fr)".unsafeCast<GridTemplateColumns>()
            gap = 1.px
            backgroundColor = Color("rgba(255, 255, 255, 0.1)")
            padding = 2.px
            borderRadius = 8.px
            aspectRatio = "1 / 2".unsafeCast<AspectRatio>()
            maxHeight = 70.vh
            boxShadow = "0 10px 40px rgba(0, 0, 0, 0.3)".unsafeCast<BoxShadow>()
        }

        for (row in 0 until boardRows) {
            for (col in 0 until boardCols) {
//                val cellType = getCellType(row, col, board, currentPiece, props.ghostY)

                div {
//                    style = jso {
//                        this.backgroundColor = when (cellType) {
//                            "empty" -> "rgba(0, 0, 0, 0.3)"
//                            "ghost" -> "rgba(255, 255, 255, 0.1)"
//                            "i" -> "rgb(0, 240, 240)"
//                            "o" -> "rgb(240, 240, 0)"
//                            "t" -> "rgb(160, 0, 240)"
//                            "s" -> "rgb(0, 240, 0)"
//                            "z" -> "rgb(240, 0, 0)"
//                            "j" -> "rgb(0, 0, 240)"
//                            "l" -> "rgb(240, 160, 0)"
//                            else -> "rgba(255, 255, 255, 0.8)"
//                        }
//                        this.border = "1px solid rgba(255, 255, 255, 0.05)"
//                        this.transition = "background-color 0.1s"
//                        this.borderRadius = "2px"
//                    }
                }
            }
        }
    }
}

private fun getCellType(
    row: Int,
    col: Int,
    gameState: GameState,
    ghostY: Int?
): String {
    val board = gameState.board
    val currentPiece = gameState.currentPiece
    val currentPosition = gameState.currentPosition

    board.cells[Position(col, row)]?.let {
        return it.name.lowercase()
    }

    currentPiece?.let { piece ->
        piece.blocks.forEach { block ->
            if (currentPosition.y + block.y == row && currentPosition.x + block.x == col) {
                return piece.type.name.lowercase()
            }
        }

        ghostY?.let { gy ->
            piece.blocks.forEach { block ->
                if (gy + block.y == row && currentPosition.x + block.x == col) {
                    return "ghost"
                }
            }
        }
    }

    return "empty"
}