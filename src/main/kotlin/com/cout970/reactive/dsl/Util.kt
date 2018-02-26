package com.cout970.reactive.dsl

import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.Renderer
import com.cout970.reactive.nodes.ComponentBuilder
import com.cout970.reactive.nodes.comp
import org.liquidengine.legui.component.Component

fun <T : Component> ComponentBuilder<T>.childrenAsNodes() {
    this.component.childComponents.forEach {
        comp(it) {
            if (!it.isEmpty) {
                childrenAsNodes()
            }
        }
    }
}

fun RBuilder.postMount(func: Component.() -> Unit) {
    val oldDeferred = this.deferred
    this.deferred = {
        it.metadata[Renderer.METADATA_POST_MOUNT] = func
        oldDeferred?.invoke(it)
    }
}