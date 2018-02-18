package com.cout970.reactive.internal.counter

import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.internal.demoWindow
import com.cout970.reactive.nodes.child
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.style.color.ColorConstants

fun main(args: Array<String>) {
    demoWindow { env ->
        Renderer.render(env.frame.container) {
            child(DemoComponent::class)
        }
    }
}

data class DemoState(val count: Int) : RState

class DemoComponent : RComponent<EmptyProps, DemoState>() {

    override fun getInitialState() = DemoState(0)
    override fun RBuilder.render() {
        +Label("You clicked me ${state.count} times!").apply {

            sizeX = 150f
            sizeY = 30f

            backgroundColor { ColorConstants.lightBlue() }
            borderless()

            onClick {
                setState { DemoState(count + 1) }
            }
        }
    }
}
