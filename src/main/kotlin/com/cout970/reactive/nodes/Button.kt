package com.cout970.reactive.nodes

import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RDescriptor
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Component


data class ButtonDescriptor(private val text: String) : RDescriptor {
    override fun mapToComponent(): Component = Button().apply { textState.text = text; }
}

class ButtonBuilder : RBuilder() {
    var text: String = ""

    override fun toDescriptor(): RDescriptor = ButtonDescriptor(text)
}

fun RBuilder.button(key: String, block: ButtonBuilder.() -> Unit = {}) =
        +ButtonBuilder().apply(block).build(key)

fun ButtonBuilder.attr(func: Button.() -> Unit) {
    deferred = { (it as Button).func() }
}