package com.yet.tetris

import com.yet.tetris.feature.root.RootComponent
import com.yet.tetris.game.GameContent
import com.yet.tetris.home.HomeContent
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.useAsState
import react.FC

val RootContent = FC<RProps<RootComponent>> { props ->
    val childStack by props.component.childStack.useAsState()
    val activeChild = childStack.active.instance

    when (val child = activeChild) {
        is RootComponent.Child.Home -> HomeContent {
            component = child.component
        }

        is RootComponent.Child.Game -> GameContent {
            component = child.component
        }
    }
}
