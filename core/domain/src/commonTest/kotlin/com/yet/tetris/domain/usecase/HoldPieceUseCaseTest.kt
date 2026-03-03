package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class HoldPieceUseCaseTest {
    private val checkCollisionUseCase = CheckCollisionUseCase()
    private val generateTetrominoUseCase = GenerateTetrominoUseCase()
    private val previewQueueEngine = PreviewQueueEngine(generateTetrominoUseCase)
    private val useCase = HoldPieceUseCase(checkCollisionUseCase, previewQueueEngine)

    @Test
    fun holds_current_piece_WHEN_hold_slot_empty() {
        val state = createState()

        val result = useCase(state)
        val applied = assertIs<HoldPieceUseCase.Result.Applied>(result)
        val nextState = applied.gameState

        assertEquals(TetrominoType.T, nextState.holdPiece?.type)
        assertEquals(0, nextState.holdPiece?.rotation)
        assertEquals(TetrominoType.I, nextState.currentPiece?.type)
        assertEquals(Position(3, 0), nextState.currentPosition)
        assertEquals(TetrominoType.O, nextState.nextPiece.type)
        assertEquals(GameState.QUEUE_SIZE, nextState.nextQueue.size)
        assertFalse(nextState.canHold)
    }

    @Test
    fun swaps_piece_WHEN_hold_slot_occupied() {
        val state =
            createState(
                holdPiece = Tetromino.create(TetrominoType.Z, rotation = 2),
            )

        val result = useCase(state)
        val applied = assertIs<HoldPieceUseCase.Result.Applied>(result)
        val nextState = applied.gameState

        assertEquals(TetrominoType.Z, nextState.currentPiece?.type)
        assertEquals(0, nextState.currentPiece?.rotation)
        assertEquals(TetrominoType.T, nextState.holdPiece?.type)
        assertEquals(TetrominoType.I, nextState.nextPiece.type)
        assertEquals(GameState.QUEUE_SIZE, nextState.nextQueue.size)
        assertFalse(nextState.canHold)
    }

    @Test
    fun returns_null_WHEN_hold_already_used() {
        val state = createState(canHold = false)

        val result = useCase(state)

        val blocked = assertIs<HoldPieceUseCase.Result.Blocked>(result)
        assertEquals(HoldPieceUseCase.BlockedReason.HOLD_ALREADY_USED, blocked.reason)
    }

    @Test
    fun marks_game_over_WHEN_swap_target_collides_on_spawn() {
        val filledSpawn =
            buildMap {
                put(Position(3, 1), TetrominoType.I)
                put(Position(4, 1), TetrominoType.I)
                put(Position(5, 1), TetrominoType.I)
                put(Position(6, 1), TetrominoType.I)
            }

        val state = createState(board = GameBoard(cells = filledSpawn))

        val result = useCase(state)
        val applied = assertIs<HoldPieceUseCase.Result.Applied>(result)
        val nextState = applied.gameState

        assertTrue(nextState.isGameOver)
        assertEquals(null, nextState.currentPiece)
    }

    private fun createState(
        board: GameBoard = GameBoard(),
        holdPiece: Tetromino? = null,
        canHold: Boolean = true,
    ): GameState =
        GameState(
            board = board,
            currentPiece = Tetromino.create(TetrominoType.T, rotation = 3),
            currentPosition = Position(5, 10),
            nextPiece = Tetromino.create(TetrominoType.I, rotation = 1),
            nextQueue =
                listOf(
                    Tetromino.create(TetrominoType.O),
                    Tetromino.create(TetrominoType.J),
                    Tetromino.create(TetrominoType.L),
                    Tetromino.create(TetrominoType.S),
                ),
            holdPiece = holdPiece,
            canHold = canHold,
        )
}
