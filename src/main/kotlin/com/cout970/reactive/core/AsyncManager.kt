package com.cout970.reactive.core

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlin.coroutines.experimental.CoroutineContext

object AsyncManager {

    private var updateCtx: CoroutineContext = newSingleThreadContext("Reactive")

    fun setCoroutineContext(ctx: CoroutineContext){
        updateCtx = ctx
    }

    fun setTimeout(time: Int, func: () -> Unit) {
        launch(updateCtx) {
            delay(time.toLong())
            func()
        }
    }

    fun runLater(function: () -> Unit) {
        setTimeout(0, function)
    }
}