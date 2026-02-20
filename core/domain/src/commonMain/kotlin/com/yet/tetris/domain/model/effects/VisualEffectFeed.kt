package com.yet.tetris.domain.model.effects

data class VisualEffectFeed(
    val sequence: Long = 0L,
    val latest: VisualEffectBurst? = null,
)
