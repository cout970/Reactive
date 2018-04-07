package com.cout970.reactive.core

object ReflectUtil {

    fun set(instance: Any, field: String, value: Any) {
        instance::class.java.getDeclaredField(field).let {
            it.isAccessible = true
            it.set(instance, value)
        }
    }
}