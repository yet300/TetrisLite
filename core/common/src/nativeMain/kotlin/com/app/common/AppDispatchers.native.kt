package com.app.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

actual val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher =
    Dispatchers.IO
