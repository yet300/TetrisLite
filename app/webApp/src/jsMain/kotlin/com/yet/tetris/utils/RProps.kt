package com.yet.tetris.utils

import react.Props

external interface RProps<T : Any> : Props {
    var component: T
}
