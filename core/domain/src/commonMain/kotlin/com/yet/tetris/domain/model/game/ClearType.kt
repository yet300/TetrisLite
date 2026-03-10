package com.yet.tetris.domain.model.game

enum class ClearType {
    NONE,
    SINGLE,
    DOUBLE,
    TRIPLE,
    TETRIS,
    T_SPIN,
    T_SPIN_SINGLE,
    T_SPIN_DOUBLE,
    T_SPIN_TRIPLE,
    ;

    val isTSpin: Boolean
        get() =
            when (this) {
                T_SPIN, T_SPIN_SINGLE, T_SPIN_DOUBLE, T_SPIN_TRIPLE -> true
                else -> false
            }

    val isBackToBackEligible: Boolean
        get() =
            when (this) {
                TETRIS, T_SPIN_SINGLE, T_SPIN_DOUBLE, T_SPIN_TRIPLE -> true
                else -> false
            }
}
