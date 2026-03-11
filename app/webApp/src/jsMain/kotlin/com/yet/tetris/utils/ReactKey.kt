package com.yet.tetris.utils

import react.Key
import kotlin.js.unsafeCast

internal fun String.reactKey(): Key = unsafeCast<Key>()
