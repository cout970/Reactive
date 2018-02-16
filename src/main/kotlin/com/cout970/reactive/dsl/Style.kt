package com.cout970.reactive.dsl

import org.joml.Vector4f
import org.liquidengine.legui.component.Component
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