package com.yet.tetris.utils

import com.arkivanov.decompose.value.Value
import js.objects.Object
import js.objects.TypedPropertyDescriptor
import react.StateInstance
import react.useEffectOnce
import react.useState

private var uniqueId: Long = 0L

internal fun Any.uniqueId(): Long {
    var id: dynamic = asDynamic().__unique_id
    if (id == undefined) {
        id = ++uniqueId

        val descriptor =
            object : TypedPropertyDescriptor<Long> {
                override var value: Long? = id
                override var writable: Boolean? = false
                override var configurable: Boolean? = true

                override var enumerable: Boolean? = null
                override var get: (() -> Long)? = null
                override var set: ((v: Long) -> Unit)? = null
            }

        Object.defineProperty(this, "__unique_id", descriptor)
    }
    return id
}

internal fun Any.uniqueKey(): String = uniqueId().toString()

internal fun <T : Any> Value<T>.useAsState(): StateInstance<T> {
    val state = useState { value }
    val (_, set) = state

    useEffectOnce {
        val cancellation = subscribe { set(it) }

        val cleanup: () -> Unit = {
            cancellation.cancel()
        }

        cleanup
    }


    return state
}
