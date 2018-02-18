package com.cout970.reactive.core

import org.liquidengine.legui.component.Component

data class RContext(val mountPoint: Component, val app: RNode) {

    internal val unmountedComponents = mutableSetOf<RComponent<*, *>>()
    internal val mountedComponents = mutableSetOf<RComponent<*, *>>()
}