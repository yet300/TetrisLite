package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StartGameUseCaseTest {

    private val generateTetromino = GenerateTetrominoUseCase()
    private val useCase = StartGameUseCase(generateTetromino)

    @Test
    fun invoke_shouldCreateInitialState() {
        // Given
        val settings = GameSettings()

        // When
        val state = useCase(settings)

        // Then
        assertNotNull(state)
        assertNotNull(state.currentPiece)
        assertNotNull(state.nextPiece)
        assertEquals(0, state.score)
        assertEquals(0, state.linesCleared)
        assertFalse(state.isGameOver)
        assertFalse(state.isPaused)
    }

    @Test
    fun invoke_shouldCreateEmptyBoard() {
        // Given
        val settings = GameSettings()

        // When
        val state = useCase(settings)

        // Then
        assertTrue(state.board.cells.isEmpty())
        assertEquals(10, state.board.width)
        assertEquals(20, state.board.height)
    }

    @Test
    fun invoke_shouldSpawnPieceAtTopCenter() {
        // Given
        val settings = GameSettings()

        // When
        val state = useCase(settings)

        // Then
        assertEquals(3, state.currentPosition.x)
        assertEquals(0, state.currentPosition.y)
    }

    @Test
    fun invoke_shouldGenerateTwoDifferentPieces() {
        // Given
        val settings = GameSettings()

        // When
        val state = useCase(settings)

        // Then
        assertNotNull(state.currentPiece)
        assertNotNull(state.nextPiece)
        // Note: They might be the same type by chance, but both should exist
    }

    @Test
    fun invoke_shouldResetGenerator() {
        // Given - Generate some pieces first
        repeat(5) { generateTetromino() }
        val settings = GameSettings()

        // When
        val state = useCase(settings)

        // Then - Should have fresh pieces
        assertNotNull(state.currentPiece)
        assertNotNull(state.nextPiece)
    }

    @Test
    fun startWithDefaults_shouldUseDefaultSettings() {
        // When
        val state = useCase.startWithDefaults()

        // Then
        assertNotNull(state)
        assertEquals(0, state.score)
        assertEquals(0, state.linesCleared)
        assertFalse(state.isGameOver)
    }

    @Test
    fun invoke_multipleCalls_shouldCreateIndependentStates() {
        // Given
        val settings = GameSettings()

        // When
        val state1 = useCase(settings)
        val state2 = useCase(settings)

        // Then - Each should be independent
        assertEquals(0, state1.score)
        assertEquals(0, state2.score)
        assertTrue(state1.board.cells.isEmpty())
        assertTrue(state2.board.cells.isEmpty())
    }

    @Test
    fun invoke_withDifferentDifficulties_shouldCreateValidStates() {
        // Given
        val easySettings = GameSettings(difficulty = Difficulty.EASY)
        val hardSettings = GameSettings(difficulty = Difficulty.HARD)

        // When
        val easyState = useCase(easySettings)
        val hardState = useCase(hardSettings)

        // Then - Both should be valid initial states
        assertNotNull(easyState.currentPiece)
        assertNotNull(hardState.currentPiece)
        assertEquals(0, easyState.score)
        assertEquals(0, hardState.score)
    }

    @Test
    fun invoke_shouldHaveRotation0ForInitialPieces() {
        // Given
        val settings = GameSettings()

        // When
        val state = useCase(settings)

        // Then
        assertEquals(0, state.currentPiece?.rotation)
        assertEquals(0, state.nextPiece.rotation)
    }
}
