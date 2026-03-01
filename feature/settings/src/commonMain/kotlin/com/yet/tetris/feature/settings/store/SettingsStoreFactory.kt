package com.yet.tetris.feature.settings.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.repository.GameSettingsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class SettingsStoreFactory
    constructor(
        private val storeFactory: StoreFactory,
        private val gameSettingsRepository: GameSettingsRepository,
    ) {
        fun create(): SettingsStore =
            object :
                SettingsStore,
                Store<SettingsStore.Intent, SettingsStore.State, SettingsStore.Label> by storeFactory.create(
                    name = "SettingsStore",
                    initialState = SettingsStore.State(),
                    bootstrapper = SimpleBootstrapper(SettingsStore.Action.SettingsLoadStarted),
                    executorFactory = ::ExecutorImpl,
                    reducer = ReducerImpl,
                ) {}

        private object ReducerImpl : Reducer<SettingsStore.State, SettingsStore.Msg> {
            override fun SettingsStore.State.reduce(msg: SettingsStore.Msg): SettingsStore.State =
                when (msg) {
                    is SettingsStore.Msg.SettingsLoaded -> copy(settings = msg.settings)
                    is SettingsStore.Msg.SettingsUpdated -> copy(settings = msg.settings)
                    is SettingsStore.Msg.SavingChanged -> copy(isSaving = msg.isSaving)
                }
        }

        private inner class ExecutorImpl :
            CoroutineExecutor<SettingsStore.Intent, SettingsStore.Action, SettingsStore.State, SettingsStore.Msg, SettingsStore.Label>() {
            private var saveJob: Job? = null

            override fun executeAction(action: SettingsStore.Action) {
                when (action) {
                    is SettingsStore.Action.SettingsLoadStarted -> loadSettings()
                }
            }

            override fun executeIntent(intent: SettingsStore.Intent) {
                val getState = state()
                when (intent) {
                    is SettingsStore.Intent.ChangeDifficulty ->
                        updateSettings(getState) {
                            it.copy(difficulty = intent.difficulty)
                        }

                    is SettingsStore.Intent.ChangeVisualTheme ->
                        updateSettings(getState) {
                            it.copy(themeConfig = it.themeConfig.copy(visualTheme = intent.theme))
                        }

                    is SettingsStore.Intent.ChangePieceStyle ->
                        updateSettings(getState) {
                            it.copy(themeConfig = it.themeConfig.copy(pieceStyle = intent.style))
                        }

                    is SettingsStore.Intent.ToggleMusic ->
                        updateSettings(getState) {
                            it.copy(audioSettings = it.audioSettings.copy(musicEnabled = intent.enabled))
                        }

                    is SettingsStore.Intent.ToggleSoundEffects ->
                        updateSettings(getState) {
                            it.copy(audioSettings = it.audioSettings.copy(soundEffectsEnabled = intent.enabled))
                        }

                    is SettingsStore.Intent.ChangeMusicVolume ->
                        updateSettings(getState) {
                            it.copy(audioSettings = it.audioSettings.copy(musicVolume = intent.volume))
                        }

                    is SettingsStore.Intent.ChangeSFXVolume ->
                        updateSettings(getState) {
                            it.copy(audioSettings = it.audioSettings.copy(sfxVolume = intent.volume))
                        }

                    is SettingsStore.Intent.ChangeMusicTheme ->
                        updateSettings(getState) {
                            it.copy(audioSettings = it.audioSettings.copy(selectedMusicTheme = intent.theme))
                        }
                }
            }

            private fun loadSettings() {
                scope.launch {
                    try {
                        val settings = gameSettingsRepository.getSettings()
                        dispatch(SettingsStore.Msg.SettingsLoaded(settings))
                    } catch (e: Exception) {
                        publish(SettingsStore.Label.ShowError(e.message ?: "Failed to load settings"))
                    }
                }
            }

            private fun updateSettings(
                state: SettingsStore.State,
                update: (GameSettings) -> GameSettings,
            ) {
                val updatedSettings = update(state.settings)
                if (updatedSettings == state.settings) {
                    return
                }
                dispatch(SettingsStore.Msg.SettingsUpdated(updatedSettings))
                scheduleSave(updatedSettings)
            }

            private fun scheduleSave(settings: GameSettings) {
                saveJob?.cancel()
                saveJob =
                    scope.launch {
                        val currentJob = coroutineContext[Job]
                        dispatch(SettingsStore.Msg.SavingChanged(true))
                        try {
                            gameSettingsRepository.saveSettings(settings)
                        } catch (_: CancellationException) {
                            // Ignore cancellation caused by a newer update
                        } catch (e: Exception) {
                            publish(
                                SettingsStore.Label.ShowError(
                                    e.message ?: "Failed to save settings",
                                ),
                            )
                        } finally {
                            if (saveJob === currentJob) {
                                saveJob = null
                                dispatch(SettingsStore.Msg.SavingChanged(false))
                            }
                        }
                    }
            }
        }
    }
