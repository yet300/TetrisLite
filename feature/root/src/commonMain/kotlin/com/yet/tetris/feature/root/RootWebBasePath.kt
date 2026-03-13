package com.yet.tetris.feature.root

object RootWebBasePath {
    private var rawValue: String = ""

    fun configure(value: String) {
        rawValue = value.trim('/')
    }

    fun current(): String = rawValue
}
