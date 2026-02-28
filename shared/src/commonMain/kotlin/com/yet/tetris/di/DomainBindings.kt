package com.yet.tetris.di

import com.yet.tetris.domain.repository.AudioRepository
import com.yet.tetris.domain.repository.GameHistoryRepository
import com.yet.tetris.domain.repository.GameSettingsRepository
import com.yet.tetris.domain.repository.GameStateRepository
import com.yet.tetris.domain.usecase.AdvanceGameTickUseCase
import com.yet.tetris.domain.usecase.CalculateGhostPositionUseCase
import com.yet.tetris.domain.usecase.CalculateScoreUseCase
import com.yet.tetris.domain.usecase.CheckCollisionUseCase
import com.yet.tetris.domain.usecase.GenerateTetrominoUseCase
import com.yet.tetris.domain.usecase.GestureHandlingUseCase
import com.yet.tetris.domain.usecase.HandleSwipeInputUseCase
import com.yet.tetris.domain.usecase.HardDropUseCase
import com.yet.tetris.domain.usecase.InitializeGameSessionUseCase
import com.yet.tetris.domain.usecase.LockPieceUseCase
import com.yet.tetris.domain.usecase.MovePieceUseCase
import com.yet.tetris.domain.usecase.PersistGameAudioUseCase
import com.yet.tetris.domain.usecase.PlanVisualFeedbackUseCase
import com.yet.tetris.domain.usecase.ProcessLockedPieceUseCase
import com.yet.tetris.domain.usecase.RotatePieceUseCase
import com.yet.tetris.domain.usecase.StartGameUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
@BindingContainer
object DomainBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun provideCheckCollisionUseCase(): CheckCollisionUseCase = CheckCollisionUseCase()

    @SingleIn(AppScope::class)
    @Provides
    fun provideCalculateScoreUseCase(): CalculateScoreUseCase = CalculateScoreUseCase()

    @SingleIn(AppScope::class)
    @Provides
    fun provideGenerateTetrominoUseCase(): GenerateTetrominoUseCase = GenerateTetrominoUseCase()

    @SingleIn(AppScope::class)
    @Provides
    fun provideMovePieceUseCase(checkCollisionUseCase: CheckCollisionUseCase): MovePieceUseCase =
        MovePieceUseCase(checkCollision = checkCollisionUseCase)

    @SingleIn(AppScope::class)
    @Provides
    fun provideRotatePieceUseCase(checkCollisionUseCase: CheckCollisionUseCase): RotatePieceUseCase =
        RotatePieceUseCase(checkCollision = checkCollisionUseCase)

    @SingleIn(AppScope::class)
    @Provides
    fun provideHardDropUseCase(checkCollisionUseCase: CheckCollisionUseCase): HardDropUseCase =
        HardDropUseCase(checkCollision = checkCollisionUseCase)

    @SingleIn(AppScope::class)
    @Provides
    fun provideLockPieceUseCase(
        calculateScoreUseCase: CalculateScoreUseCase,
        generateTetrominoUseCase: GenerateTetrominoUseCase,
        checkCollisionUseCase: CheckCollisionUseCase,
    ): LockPieceUseCase =
        LockPieceUseCase(
            calculateScore = calculateScoreUseCase,
            generateTetromino = generateTetrominoUseCase,
            checkCollision = checkCollisionUseCase,
        )

    @SingleIn(AppScope::class)
    @Provides
    fun provideStartGameUseCase(generateTetrominoUseCase: GenerateTetrominoUseCase): StartGameUseCase =
        StartGameUseCase(generateTetromino = generateTetrominoUseCase)

    @SingleIn(AppScope::class)
    @Provides
    fun provideHandleSwipeInputUseCase(
        movePieceUseCase: MovePieceUseCase,
        hardDropUseCase: HardDropUseCase,
    ): HandleSwipeInputUseCase =
        HandleSwipeInputUseCase(
            movePiece = movePieceUseCase,
            hardDrop = hardDropUseCase,
        )

    @SingleIn(AppScope::class)
    @Provides
    fun provideCalculateGhostPositionUseCase(): CalculateGhostPositionUseCase = CalculateGhostPositionUseCase()

    @SingleIn(AppScope::class)
    @Provides
    fun providePlanVisualFeedbackUseCase(): PlanVisualFeedbackUseCase = PlanVisualFeedbackUseCase()

    @Provides
    fun provideGestureHandlingUseCase(): GestureHandlingUseCase = GestureHandlingUseCase()

    @Provides
    fun provideAdvanceGameTickUseCase(
        movePieceUseCase: MovePieceUseCase,
        calculateGhostPositionUseCase: CalculateGhostPositionUseCase,
    ): AdvanceGameTickUseCase =
        AdvanceGameTickUseCase(
            movePieceUseCase = movePieceUseCase,
            calculateGhostPositionUseCase = calculateGhostPositionUseCase,
        )

    @Provides
    fun providePersistGameAudioUseCase(
        gameStateRepository: GameStateRepository,
        gameHistoryRepository: GameHistoryRepository,
        audioRepository: AudioRepository,
    ): PersistGameAudioUseCase =
        PersistGameAudioUseCase(
            gameStateRepository = gameStateRepository,
            gameHistoryRepository = gameHistoryRepository,
            audioRepository = audioRepository,
        )

    @Provides
    fun provideInitializeGameSessionUseCase(
        gameSettingsRepository: GameSettingsRepository,
        gameStateRepository: GameStateRepository,
        startGameUseCase: StartGameUseCase,
    ): InitializeGameSessionUseCase =
        InitializeGameSessionUseCase(
            gameSettingsRepository = gameSettingsRepository,
            gameStateRepository = gameStateRepository,
            startGameUseCase = startGameUseCase,
        )

    @Provides
    fun provideProcessLockedPieceUseCase(
        lockPieceUseCase: LockPieceUseCase,
        planVisualFeedbackUseCase: PlanVisualFeedbackUseCase,
        advanceGameTickUseCase: AdvanceGameTickUseCase,
    ): ProcessLockedPieceUseCase =
        ProcessLockedPieceUseCase(
            lockPieceUseCase = lockPieceUseCase,
            planVisualFeedbackUseCase = planVisualFeedbackUseCase,
            advanceGameTickUseCase = advanceGameTickUseCase,
        )
}
