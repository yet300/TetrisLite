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
import com.yet.tetris.domain.model.settings.KeyboardLayout
import com.yet.tetris.domain.model.settings.SwipeLayout
import com.yet.tetris.domain.model.settings.SwipeSensitivity
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.VisualTheme
import com.yet.tetris.feature.settings.store.SettingsStore
import com.yet.tetris.feature.settings.store.SettingsStoreFactory
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class DefaultSettingsComponent(
    componentContext: ComponentContext,
    private val onSettingsSaved: () -> Unit,
    private val onDismiss: () -> Unit,
) : ComponentContext by componentContext,
    SettingsComponent,
    KoinComponent {
    private val store = instanceKeeper.getStore { SettingsStoreFactory().create() }

    init {
        coroutineScope().launch {
            store.labels.collect {
                when (it) {
                    is SettingsStore.Label.SettingsSaved -> {
                        onSettingsSaved()
                        onDismiss()
                    }
                    is SettingsStore.Label.ChangesDiscarded -> onDismiss()
                    is SettingsStore.Label.ShowError -> {
                        // Handle error
                    }
                }
            }
        }
    }

    override val model: Value<SettingsComponent.Model> =
        store.asValue().map { state ->
            SettingsComponent.Model(
                settings = state.settings,
                isSaving = state.isSaving,
                hasUnsavedChanges = state.hasUnsavedChanges,
            )
        }

    override fun onDifficultyChanged(difficulty: Difficulty) {
        store.accept(SettingsStore.Intent.ChangeDifficulty(difficulty))
    }

    override fun onVisualThemeChanged(theme: VisualTheme) {
        store.accept(SettingsStore.Intent.ChangeVisualTheme(theme))
    }

    override fun onPieceStyleChanged(style: PieceStyle) {
        store.accept(SettingsStore.Intent.ChangePieceStyle(style))
    }

    override fun onKeyboardLayoutChanged(layout: KeyboardLayout) {
        store.accept(SettingsStore.Intent.ChangeKeyboardLayout(layout))
    }

    override fun onSwipeLayoutChanged(layout: SwipeLayout) {
        store.accept(SettingsStore.Intent.ChangeSwipeLayout(layout))
    }

    override fun onSwipeSensitivityChanged(sensitivity: SwipeSensitivity) {
        store.accept(SettingsStore.Intent.ChangeSwipeSensitivity(sensitivity))
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

    override fun onSave() {
        store.accept(SettingsStore.Intent.SaveSettings)
    }

    override fun onDiscard() {
        store.accept(SettingsStore.Intent.DiscardChanges)
    }
}
