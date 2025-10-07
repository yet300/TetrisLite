package com.yet.tetris.feature.settings.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.repository.GameSettingsRepository
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
internal class SettingsStoreFactory(
    private val storeFactory: StoreFactory,
    private val gameSettingsRepository: GameSettingsRepository
) {

    fun create(): SettingsStore =
        object : SettingsStore,
            Store<SettingsStore.Intent, SettingsStore.State, SettingsStore.Label> by storeFactory.create(
                name = "SettingsStore",
                initialState = SettingsStore.State(),
                bootstrapper = SimpleBootstrapper(SettingsStore.Action.SettingsLoadStarted),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}



    private object ReducerImpl : Reducer<SettingsStore.State, SettingsStore.Msg> {
        override fun SettingsStore.State.reduce(msg: SettingsStore.Msg): SettingsStore.State =
            when (msg) {
                is SettingsStore.Msg.SettingsLoaded -> copy(settings = msg.settings)
                is SettingsStore.Msg.SettingsUpdated -> copy(settings = msg.settings)
                is SettingsStore.Msg.SavingChanged -> copy(isSaving = msg.isSaving)
                is SettingsStore.Msg.UnsavedChangesChanged -> copy(hasUnsavedChanges = msg.hasUnsavedChanges)
            }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<SettingsStore.Intent, SettingsStore.Action, SettingsStore.State, SettingsStore.Msg, SettingsStore.Label>() {

        private var originalSettings: GameSettings? = null


        override fun executeAction(action: SettingsStore.Action) {
            when (action) {
                is SettingsStore.Action.SettingsLoadStarted -> loadSettings()
            }
        }

        override fun executeIntent(intent: SettingsStore.Intent) {
            val getState = state()
            when (intent) {
                is SettingsStore.Intent.ChangeDifficulty -> updateSettings(getState) {
                    it.copy(difficulty = intent.difficulty)
                }

                is SettingsStore.Intent.ChangeTetrominoColor -> updateSettings(getState) {
                    val updatedColors = it.tetrominoColors.toMutableMap()
                    updatedColors[intent.type] = intent.color
                    it.copy(tetrominoColors = updatedColors)
                }

                is SettingsStore.Intent.ChangeBackgroundColor -> updateSettings(getState) {
                    it.copy(backgroundColor = intent.color)
                }

                is SettingsStore.Intent.ChangeKeyboardLayout -> updateSettings(getState) {
                    it.copy(keyboardLayout = intent.layout)
                }

                is SettingsStore.Intent.ChangeSwipeLayout -> updateSettings(getState) {
                    it.copy(swipeLayout = intent.layout)
                }

                is SettingsStore.Intent.ChangeSwipeSensitivity -> updateSettings(getState) {
                    it.copy(swipeSensitivity = intent.sensitivity)
                }

                is SettingsStore.Intent.ToggleMusic -> updateSettings(getState) {
                    it.copy(audioSettings = it.audioSettings.copy(musicEnabled = intent.enabled))
                }

                is SettingsStore.Intent.ToggleSoundEffects -> updateSettings(getState) {
                    it.copy(audioSettings = it.audioSettings.copy(soundEffectsEnabled = intent.enabled))
                }

                is SettingsStore.Intent.ChangeMusicVolume -> updateSettings(getState) {
                    it.copy(audioSettings = it.audioSettings.copy(musicVolume = intent.volume))
                }

                is SettingsStore.Intent.ChangeSFXVolume -> updateSettings(getState) {
                    it.copy(audioSettings = it.audioSettings.copy(sfxVolume = intent.volume))
                }

                is SettingsStore.Intent.ChangeMusicTheme -> updateSettings(getState) {
                    it.copy(audioSettings = it.audioSettings.copy(selectedMusicTheme = intent.theme))
                }

                is SettingsStore.Intent.SaveSettings -> saveSettings(getState)
                is SettingsStore.Intent.DiscardChanges -> discardChanges()
            }
        }

        private fun loadSettings() {
            scope.launch {
                try {
                    val settings = gameSettingsRepository.getSettings()
                    originalSettings = settings
                    dispatch(SettingsStore.Msg.SettingsLoaded(settings))
                } catch (e: Exception) {
                    publish(SettingsStore.Label.ShowError(e.message ?: "Failed to load settings"))
                }
            }
        }

        private fun updateSettings(
            state: SettingsStore.State,
            update: (GameSettings) -> GameSettings
        ) {
            val updatedSettings = update(state.settings)
            dispatch(SettingsStore.Msg.SettingsUpdated(updatedSettings))
            dispatch(SettingsStore.Msg.UnsavedChangesChanged(updatedSettings != originalSettings))
        }

        private fun saveSettings(state: SettingsStore.State) {
            scope.launch {
                try {
                    dispatch(SettingsStore.Msg.SavingChanged(true))

                    gameSettingsRepository.saveSettings(state.settings)
                    originalSettings = state.settings

                    dispatch(SettingsStore.Msg.SavingChanged(false))
                    dispatch(SettingsStore.Msg.UnsavedChangesChanged(false))
                    publish(SettingsStore.Label.SettingsSaved)
                } catch (e: Exception) {
                    dispatch(SettingsStore.Msg.SavingChanged(false))
                    publish(SettingsStore.Label.ShowError(e.message ?: "Failed to save settings"))
                }
            }
        }

        private fun discardChanges() {
            originalSettings?.let { original ->
                dispatch(SettingsStore.Msg.SettingsUpdated(original))
                dispatch(SettingsStore.Msg.UnsavedChangesChanged(false))
                publish(SettingsStore.Label.ChangesDiscarded)
            }
        }
    }
}
