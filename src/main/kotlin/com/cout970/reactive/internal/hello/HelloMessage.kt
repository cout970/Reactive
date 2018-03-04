package com.cout970.reactive.internal.hello

import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.core.Renderer
import com.cout970.reactive.dsl.*
import com.cout970.reactive.internal.demoWindow
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.label
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.optional.align.HorizontalAlign

fun main(args: Array<String>) {
    demoWindow { env ->
        Renderer.render(env.frame.container) {
            child(HelloMessage::class, HelloMessageProps("Taylor"))
        }
    }
}

data class HelloMessageProps(val name: String) : RProps

class HelloMessage : RStatelessComponent<HelloMessageProps>() {

    override fun RBuilder.render() {
        div {
            postMount {
                sizeX = 150f
                sizeY = 50f
                center()
            }

            label("Hello ${props.name}"){

                style {
                    horizontalAlign = HorizontalAlign.CENTER
                }

                postMount {
                    centerX()
                }
            }
        }
    }
}