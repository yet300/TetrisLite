package com.yet.tetris.feature.root.di

import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.feature.home.HomeComponent
import com.yet.tetris.feature.root.DefaultRootComponentFactory
import com.yet.tetris.feature.root.RootComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
@BindingContainer
abstract class RootBindings {
    @Binds
    internal abstract val DefaultRootComponentFactory.bindRootComponentFactory: RootComponent.Factory

    companion object {
        @Provides
        internal fun provideDefaultRootComponentFactory(
            homeComponentFactory: HomeComponent.Factory,
            gameComponentFactory: GameComponent.Factory,
        ): DefaultRootComponentFactory =
            DefaultRootComponentFactory(
                homeComponentFactory = homeComponentFactory,
                gameComponentFactory = gameComponentFactory,
            )
    }
}
