package com.yet.tetris.feature.settings

import com.app.common.decompose.asValue
import com.app.common.decompose.coroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.VisualTheme
import com.yet.tetris.feature.settings.integration.stateToModel
import com.yet.tetris.feature.settings.store.SettingsStore
import com.yet.tetris.feature.settings.store.SettingsStoreFactory
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

internal class DefaultSettingsComponent(
    componentContext: ComponentContext,
    private val onCloseRequest: () -> Unit,
    private val settingsStoreFactory: SettingsStoreFactory,
) : ComponentContext by componentContext,
    SettingsComponent {
    private val store = instanceKeeper.getStore { settingsStoreFactory.create() }

    init {
        coroutineScope().launch {
            store.labels.collect {
                when (it) {
                    is SettingsStore.Label.ShowError -> {
                        // Handle error
                    }
                }
            }
        }
    }

    override val model: Value<SettingsComponent.Model> =
        store.asValue().map(stateToModel)

    override fun onDifficultyChanged(difficulty: Difficulty) {
        store.accept(SettingsStore.Intent.ChangeDifficulty(difficulty))
    }

    override fun onVisualThemeChanged(theme: VisualTheme) {
        store.accept(SettingsStore.Intent.ChangeVisualTheme(theme))
    }

    override fun onPieceStyleChanged(style: PieceStyle) {
        store.accept(SettingsStore.Intent.ChangePieceStyle(style))
    }

    override fun onMusicToggled(enabled: Boolean) {
        store.accept(SettingsStore.Intent.ToggleMusic(enabled))
    }

    override fun onSoundEffectsToggled(enabled: Boolean) {
        store.accept(SettingsStore.Intent.ToggleSoundEffects(enabled))
    }

    override fun onMusicVolumeChanged(volume: Float) {
        store.accept(SettingsStore.Intent.ChangeMusicVolume(volume))
    }

    override fun onSFXVolumeChanged(volume: Float) {
        store.accept(SettingsStore.Intent.ChangeSFXVolume(volume))
    }

    override fun onMusicThemeChanged(theme: MusicTheme) {
        store.accept(SettingsStore.Intent.ChangeMusicTheme(theme))
    }

    override fun onClose() {
        onCloseRequest()
    }
}

@Factory
internal class DefaultSettingsComponentFactory
@Inject
constructor(
    private val settingsStoreFactory: SettingsStoreFactory,
) : SettingsComponent.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        onCloseRequest: () -> Unit,
    ): SettingsComponent =
        DefaultSettingsComponent(
            componentContext = componentContext,
            onCloseRequest = onCloseRequest,
            settingsStoreFactory = settingsStoreFactory,
        )
}
