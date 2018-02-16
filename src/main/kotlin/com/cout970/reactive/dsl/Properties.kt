package com.cout970.reactive.dsl

import org.liquidengine.legui.component.Component

var Component.posX
    get() = position.x
    set(x) {
        position.x = x
    }

var Component.posY
    get() = position.y
    set(y) {
        position.y = y
    }

var Component.sizeX
    get() = size.x
    set(x) {
        size.x = x
    }

var Component.sizeY
    get() = size.y
    set(y) {
        size.y = y
    }


var Component.width
    get() = size.x
    set(x) {
        size.x = x
    }

var Component.height
    get() = size.y
    set(y) {
        size.y = y
    }


fun Component.enable() {
    isEnabled = true
}

fun Component.disable() {
    isEnabled = false
}

fun Component.visible() {
    isVisible = true
}

fun Component.invisible() {
    isVisible = false
}

fun Component.hide() {
    isEnabled = false
    isVisible = false
}

fun Component.show() {
    isEnabled = true
    isVisible = true
}
