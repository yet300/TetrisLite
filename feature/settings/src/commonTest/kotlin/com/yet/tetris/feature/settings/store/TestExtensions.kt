package com.yet.tetris.feature.settings.store

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope

/**
 * Collects all emitted labels from a Flow for testing purposes.
 * Returns a list that will be populated as labels are emitted.
 */
fun <T> Flow<T>.test(): MutableList<T> {
    val list = ArrayList<T>()
    @Suppress("OPT_IN_USAGE")
    GlobalScope.launch(Dispatchers.Unconfined) { collect { list += it } }

    return list
}