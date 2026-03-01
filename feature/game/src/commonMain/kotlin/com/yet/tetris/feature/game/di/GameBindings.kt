package com.yet.tetris.feature.game.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.yet.tetris.domain.repository.GameSettingsRepository
import com.yet.tetris.domain.usecase.AdvanceGameTickUseCase
import com.yet.tetris.domain.usecase.GestureHandlingUseCase
import com.yet.tetris.domain.usecase.HandleSwipeInputUseCase
import com.yet.tetris.domain.usecase.HardDropUseCase
import com.yet.tetris.domain.usecase.InitializeGameSessionUseCase
import com.yet.tetris.domain.usecase.MovePieceUseCase
import com.yet.tetris.domain.usecase.PersistGameAudioUseCase
import com.yet.tetris.domain.usecase.ProcessLockedPieceUseCase
import com.yet.tetris.domain.usecase.RotatePieceUseCase
import com.yet.tetris.feature.game.DefaultGameComponentFactory
import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.feature.game.store.GameStoreFactory
import com.yet.tetris.feature.settings.SettingsComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
@BindingContainer
abstract class GameBindings {
    @Binds
    internal abstract val DefaultGameComponentFactory.bindGameComponentFactory: GameComponent.Factory

    companion object {
        @Provides
        internal fun provideGameStoreFactory(
            storeFactory: StoreFactory,
            gameSettingsRepository: GameSettingsRepository,
            movePieceUseCase: MovePieceUseCase,
            rotatePieceUseCase: RotatePieceUseCase,
            hardDropUseCase: HardDropUseCase,
            handleSwipeInputUseCase: HandleSwipeInputUseCase,
            gestureHandlingUseCase: GestureHandlingUseCase,
            initializeGameSessionUseCase: InitializeGameSessionUseCase,
            advanceGameTickUseCase: AdvanceGameTickUseCase,
            processLockedPieceUseCase: ProcessLockedPieceUseCase,
            persistGameAudioUseCase: PersistGameAudioUseCase,
        ): GameStoreFactory =
            GameStoreFactory(
                storeFactory = storeFactory,
                gameSettingsRepository = gameSettingsRepository,
                movePieceUseCase = movePieceUseCase,
                rotatePieceUseCase = rotatePieceUseCase,
                hardDropUseCase = hardDropUseCase,
                handleSwipeInputUseCase = handleSwipeInputUseCase,
                gestureHandlingUseCase = gestureHandlingUseCase,
                initializeGameSessionUseCase = initializeGameSessionUseCase,
                advanceGameTickUseCase = advanceGameTickUseCase,
                processLockedPieceUseCase = processLockedPieceUseCase,
                persistGameAudioUseCase = persistGameAudioUseCase,
            )

        @Provides
        internal fun provideDefaultGameComponentFactory(
            gameStoreFactory: GameStoreFactory,
            settingsComponentFactory: SettingsComponent.Factory,
        ): DefaultGameComponentFactory =
            DefaultGameComponentFactory(
                gameStoreFactory = gameStoreFactory,
                settingsComponentFactory = settingsComponentFactory,
            )
    }
}
