package com.cout970.reactive.internal

import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.attr
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.label
import org.joml.Vector2f
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.style.color.ColorConstants.green
import org.liquidengine.legui.style.color.ColorConstants.red

fun main(args: Array<String>) {

    LeguiEnvironment(windowSize = Vector2f(400f, 200f)).let { env ->

        env.frame.container.size = Vector2f(400f, 200f)

        val ctx = Renderer.render(env.frame.container) {
            child(ExampleButton::class, ExampleButton.Props("Hi"))
        }

        env.context.isDebugEnabled = true

        updateOnResize(ctx, env)

        env.loop()
        env.finalice()
    }
}

fun updateOnResize(ctx: RContext, env: LeguiEnvironment) {
    var lastTime = System.currentTimeMillis()
    // Add callback for window resize
    env.keeper.chainFramebufferSizeCallback.add { _, _, _ ->
        // limit resize count to 1 per second
        val now = System.currentTimeMillis()
        if (now - lastTime > 1000) {
            lastTime = now

            // Rerender the screen
            AsyncManager.runLater { Renderer.rerender(ctx) }
        }
    }
}

class ExampleButton : RComponent<ExampleButton.Props, ExampleButton.State>() {

    override fun getInitialState() = State(false)

    override fun RBuilder.render() = div("ToggleButton") {

        attr {
            sizeX = 500f
            sizeY = 200f
        }

        label(props.text) {
            attr {
                backgroundColor { if (state.on) green() else red() }
                cornerRadius(0f)
                sizeX = 150f
                sizeY = 30f

                posX = 20f
                posY = 20f

                textState.apply {
                    horizontalAlign = HorizontalAlign.CENTER
                    fontSize = 20f
                }
            }

            onClick { setState { State(!on) } }
        }

    }

    data class Props(val text: String) : RProps
    data class State(val on: Boolean) : RState
}



