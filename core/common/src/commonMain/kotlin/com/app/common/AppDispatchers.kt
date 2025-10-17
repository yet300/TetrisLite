package com.app.common

import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

@Singleton
class AppDispatchers(
    val default: CoroutineDispatcher = Dispatchers.Default,
    val io: CoroutineDispatcher = ioDispatcher,
    val main: MainCoroutineDispatcher = Dispatchers.Main,
    val unconfined: CoroutineDispatcher = Dispatchers.Unconfined,
)

expect val ioDispatcher: CoroutineDispatcher
