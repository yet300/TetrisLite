package com.yet.tetris

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform