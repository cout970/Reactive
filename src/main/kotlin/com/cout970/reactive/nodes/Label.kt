package com.cout970.reactive.nodes

import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RDescriptor
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Label

class LabelDescriptor(val text: String) : RDescriptor {
    override fun mapToComponent(): Component = Label(text)
}

class LabelBuilder(var text: String) : RBuilder() {
    override fun toDescriptor(): RDescriptor = LabelDescriptor(text)
}

fun RBuilder.label(text: String = "", key: String? = null, block: LabelBuilder.() -> Unit = {}) =
        +LabelBuilder(text).apply(block).build(key)

fun LabelBuilder.attr(func: Label.() -> Unit) {
    deferred = { (it as Label).func() }
}