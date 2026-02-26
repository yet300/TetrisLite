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
import jakarta.inject.Singleton
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Provided

@Module
class DomainModule {
    @Singleton
    fun provideCheckCollisionUseCase(): CheckCollisionUseCase = CheckCollisionUseCase()

    @Singleton
    fun provideCalculateScoreUseCase(): CalculateScoreUseCase = CalculateScoreUseCase()

    @Singleton
    fun provideGenerateTetrominoUseCase(): GenerateTetrominoUseCase = GenerateTetrominoUseCase()

    @Singleton
    fun provideMovePieceUseCase(checkCollisionUseCase: CheckCollisionUseCase): MovePieceUseCase =
        MovePieceUseCase(checkCollision = checkCollisionUseCase)

    @Singleton
    fun provideRotatePieceUseCase(checkCollisionUseCase: CheckCollisionUseCase): RotatePieceUseCase =
        RotatePieceUseCase(checkCollision = checkCollisionUseCase)

    @Singleton
    fun provideHardDropUseCase(checkCollisionUseCase: CheckCollisionUseCase): HardDropUseCase =
        HardDropUseCase(checkCollision = checkCollisionUseCase)

    @Singleton
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

    @Singleton
    fun provideStartGameUseCase(generateTetrominoUseCase: GenerateTetrominoUseCase): StartGameUseCase =
        StartGameUseCase(generateTetromino = generateTetrominoUseCase)

    @Singleton
    fun provideHandleSwipeInputUseCase(
        movePieceUseCase: MovePieceUseCase,
        hardDropUseCase: HardDropUseCase,
    ): HandleSwipeInputUseCase =
        HandleSwipeInputUseCase(
            movePiece = movePieceUseCase,
            hardDrop = hardDropUseCase,
        )

    @Singleton
    fun provideCalculateGhostPositionUseCase(): CalculateGhostPositionUseCase =
        CalculateGhostPositionUseCase()

    @Singleton
    fun provideGestureHandlingUseCase(): GestureHandlingUseCase = GestureHandlingUseCase()

    @Singleton
    fun providePlanVisualFeedbackUseCase(): PlanVisualFeedbackUseCase = PlanVisualFeedbackUseCase()

    @Factory
    fun provideAdvanceGameTickUseCase(
        @Provided movePieceUseCase: MovePieceUseCase,
        @Provided calculateGhostPositionUseCase: CalculateGhostPositionUseCase,
    ): AdvanceGameTickUseCase = AdvanceGameTickUseCase(
        movePieceUseCase = movePieceUseCase,
        calculateGhostPositionUseCase = calculateGhostPositionUseCase
    )

    @Factory
    fun providePersistGameAudioUseCase(
        @Provided gameStateRepository: GameStateRepository,
        @Provided gameHistoryRepository: GameHistoryRepository,
        @Provided audioRepository: AudioRepository
    ) = PersistGameAudioUseCase(
        gameStateRepository = gameStateRepository,
        gameHistoryRepository = gameHistoryRepository,
        audioRepository = audioRepository
    )

    @Factory
    fun provideInitializeGameSessionUseCase(
        @Provided gameSettingsRepository: GameSettingsRepository,
        @Provided gameStateRepository: GameStateRepository,
        @Provided startGameUseCase: StartGameUseCase
    ): InitializeGameSessionUseCase =
        InitializeGameSessionUseCase(
            gameSettingsRepository = gameSettingsRepository,
            gameStateRepository = gameStateRepository,
            startGameUseCase = startGameUseCase
        )

    @Factory
    fun provideProcessLockedPieceUseCase(
        @Provided lockPieceUseCase: LockPieceUseCase,
        @Provided planVisualFeedbackUseCase: PlanVisualFeedbackUseCase,
        @Provided advanceGameTickUseCase: AdvanceGameTickUseCase
    ): ProcessLockedPieceUseCase = ProcessLockedPieceUseCase(
        lockPieceUseCase = lockPieceUseCase,
        planVisualFeedbackUseCase = planVisualFeedbackUseCase,
        advanceGameTickUseCase = advanceGameTickUseCase,
    )

}
