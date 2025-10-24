package com.yet.tetris.ui.view.game.gestures

import js.objects.unsafeJso
import react.RefObject
import web.html.HTMLCanvasElement

@OptIn(ExperimentalWasmJsInterop::class)
object GestureHandler {
    fun setupGestureListeners(
        canvas: HTMLCanvasElement,
        lastPosRef: RefObject<dynamic>,
        didStartDraggingRef: RefObject<Boolean>,
        startTimeRef: RefObject<Double>,
        totalDragRef: RefObject<dynamic>,
        onDragStarted: (() -> Unit)?,
        onDragged: ((Float, Float) -> Unit)?,
        onDragEnded: (() -> Unit)?,
        onTap: (() -> Unit)?
    ): () -> Unit {
        val handleStart: (dynamic) -> Unit = { event ->
            event.preventDefault()

            val clientX = if (event.type == "touchstart" && event.touches.length > 0) {
                event.touches[0].clientX.unsafeCast<Double>()
            } else {
                event.clientX.unsafeCast<Double>()
            }

            val clientY = if (event.type == "touchstart" && event.touches.length > 0) {
                event.touches[0].clientY.unsafeCast<Double>()
            } else {
                event.clientY.unsafeCast<Double>()
            }

            startTimeRef.current = js("Date").now().unsafeCast<Double>()
            lastPosRef.current = unsafeJso {
                this.x = clientX
                this.y = clientY
            }
            totalDragRef.current = unsafeJso {
                this.x = 0.0
                this.y = 0.0
            }
            didStartDraggingRef.current = false
        }

        val handleMove: (dynamic) -> Unit = { event ->
            event.preventDefault()
            val lastPos = lastPosRef.current
            if (lastPos != null) {
                val clientX = if (event.type == "touchmove" && event.touches.length > 0) {
                    event.touches[0].clientX.unsafeCast<Double>()
                } else {
                    event.clientX.unsafeCast<Double>()
                }

                val clientY = if (event.type == "touchmove" && event.touches.length > 0) {
                    event.touches[0].clientY.unsafeCast<Double>()
                } else {
                    event.clientY.unsafeCast<Double>()
                }

                val deltaX = clientX - lastPos.x.unsafeCast<Double>()
                val deltaY = clientY - lastPos.y.unsafeCast<Double>()

                // Accumulate total drag
                val total = totalDragRef.current
                if (total != null) {
                    total.x = total.x.unsafeCast<Double>() + kotlin.math.abs(deltaX)
                    total.y = total.y.unsafeCast<Double>() + kotlin.math.abs(deltaY)

                    // Check if we've exceeded threshold
                    if (didStartDraggingRef.current != true) {
                        val totalDist = total.x.unsafeCast<Double>() + total.y.unsafeCast<Double>()
                        if (totalDist > 5.0) {
                            onDragStarted?.invoke()
                            didStartDraggingRef.current = true
                        }
                    }
                }

                if (didStartDraggingRef.current == true) {
                    // Increase sensitivity by multiplying deltas
                    onDragged?.invoke((deltaX * 1.5).toFloat(), (deltaY * 1.5).toFloat())
                }

                lastPosRef.current = unsafeJso {
                    this.x = clientX
                    this.y = clientY
                }
            }
        }

        val handleEnd: (dynamic) -> Unit = { event ->
            event.preventDefault()
            val lastPos = lastPosRef.current
            if (lastPos != null) {
                val elapsed = js("Date").now().unsafeCast<Double>() - (startTimeRef.current ?: 0.0)
                val total = totalDragRef.current
                val totalDist = if (total != null) {
                    total.x.unsafeCast<Double>() + total.y.unsafeCast<Double>()
                } else {
                    0.0
                }

                if (didStartDraggingRef.current != true && elapsed < 300 && totalDist < 10) {
                    // Quick tap with minimal movement - rotate
                    onTap?.invoke()
                } else if (didStartDraggingRef.current == true) {
                    onDragEnded?.invoke()
                }
            }
            lastPosRef.current = null
            totalDragRef.current = null
            didStartDraggingRef.current = false
        }

        val handleCancel: (dynamic) -> Unit = { _ ->
            if (didStartDraggingRef.current == true) {
                onDragEnded?.invoke()
            }
            lastPosRef.current = null
            totalDragRef.current = null
            didStartDraggingRef.current = false
        }

        // Add event listeners with passive: false
        val options = unsafeJso<dynamic> {
            this.passive = false
        }

        canvas.asDynamic().addEventListener("mousedown", handleStart, options)
        canvas.asDynamic().addEventListener("mousemove", handleMove, options)
        canvas.asDynamic().addEventListener("mouseup", handleEnd, options)
        canvas.asDynamic().addEventListener("mouseleave", handleCancel, options)
        canvas.asDynamic().addEventListener("touchstart", handleStart, options)
        canvas.asDynamic().addEventListener("touchmove", handleMove, options)
        canvas.asDynamic().addEventListener("touchend", handleEnd, options)
        canvas.asDynamic().addEventListener("touchcancel", handleCancel, options)

        // Return cleanup function
        return {
            canvas.asDynamic().removeEventListener("mousedown", handleStart)
            canvas.asDynamic().removeEventListener("mousemove", handleMove)
            canvas.asDynamic().removeEventListener("mouseup", handleEnd)
            canvas.asDynamic().removeEventListener("mouseleave", handleCancel)
            canvas.asDynamic().removeEventListener("touchstart", handleStart)
            canvas.asDynamic().removeEventListener("touchmove", handleMove)
            canvas.asDynamic().removeEventListener("touchend", handleEnd)
            canvas.asDynamic().removeEventListener("touchcancel", handleCancel)
        }
    }
}
