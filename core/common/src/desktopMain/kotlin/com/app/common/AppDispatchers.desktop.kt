package com.app.common

import kotlinx.coroutines.Dispatchers

actual val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher =
    Dispatchers.IO
