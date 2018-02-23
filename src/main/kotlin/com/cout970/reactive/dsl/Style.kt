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
    style.cornerRadius.set(0f)
}

var Component.borderSize: Float
    get() = (style.border as? SimpleLineBorder)?.thickness ?: 0f
    set(value) {
        (style.border as? SimpleLineBorder)?.thickness = value
    }

fun Component.borderColor(func: () -> Vector4f) {
    (style.border as? SimpleLineBorder)?.color = func()
}

fun Component.cornerRadius(amount: Float) {
    style.cornerRadius.set(amount, amount, amount, amount)
}

fun Component.padding(amount: Float) {
    if (style.padding == null) style.padding = Vector4f()
    style.padding.set(amount, amount, amount, amount)
}

fun Component.padding(left: Float, top: Float, right: Float, bottom: Float) {
    if (style.padding == null) style.padding = Vector4f()
    style.padding.set(left, top, right, bottom)
}

fun Component.paddingTop(amount: Float) {
    if (style.padding == null) style.padding = Vector4f()
    style.padding.y = amount
}

fun Component.paddingBottom(amount: Float) {
    if (style.padding == null) style.padding = Vector4f()
    style.padding.w = amount
}

fun Component.paddingLeft(amount: Float) {
    if (style.padding == null) style.padding = Vector4f()
    style.padding.x = amount
}

fun Component.paddingRight(amount: Float) {
    if (style.padding == null) style.padding = Vector4f()
    style.padding.z = amount
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