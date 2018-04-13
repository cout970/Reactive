package com.cout970.reactive.dsl

import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextComponent
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.component.optional.align.VerticalAlign
import org.liquidengine.legui.icon.Icon
import org.liquidengine.legui.style.border.SimpleLineBorder
import org.liquidengine.legui.style.color.ColorConstants

fun Component.backgroundColor(func: () -> Vector4f) {
    style.background.color = func()
}

fun Component.backgroundIcon(func: () -> Icon) {
    style.background.icon = func()
}

fun Component.transparent() {
    style.background.color = ColorConstants.transparent()
}

fun Component.borderless() {
    style.border = null
}

fun Component.rectCorners() {
    style.setBorderRadius(0f)
}

var Component.borderSize: Float
    get() = (style.border as? SimpleLineBorder)?.thickness ?: 0f
    set(value) {
        (style.border as? SimpleLineBorder)?.thickness = value
    }

fun Component.borderColor(func: () -> Vector4f) {
    (style.border as? SimpleLineBorder)?.color = func()
}

fun Component.borderRadius(amount: Float) {
    style.borderRadius.set(amount, amount, amount, amount)
}

fun Component.padding(amount: Float) {
    padding(amount, amount, amount, amount)
}

fun Component.padding(left: Float, top: Float, right: Float, bottom: Float) {
    style.paddingTop = top
    style.paddingLeft = left
    style.paddingRight = right
    style.paddingBottom = bottom
}

fun Component.paddingTop(amount: Float) {
    style.paddingTop = amount
}

fun Component.paddingBottom(amount: Float) {
    style.paddingBottom = amount
}

fun Component.paddingLeft(amount: Float) {
    style.paddingLeft = amount
}

fun Component.paddingRight(amount: Float) {
    style.paddingRight = amount
}

var TextComponent.fontSize: Float
    get() = textState.fontSize
    set(value) {
        textState.fontSize = value
    }

var TextComponent.font: String
    get() = textState.font
    set(value) {
        textState.font = value
    }

var TextComponent.horizontalAlign: HorizontalAlign
    get() = textState.horizontalAlign
    set(value) {
        textState.horizontalAlign = value
    }

var TextComponent.verticalAlign: VerticalAlign
    get() = textState.verticalAlign
    set(value) {
        textState.verticalAlign = value
    }

fun TextComponent.textColor(func: () -> Vector4f) {
    textState.textColor = func()
}

fun TextComponent.highlightColor(func: () -> Vector4f) {
    textState.highlightColor = func()
}