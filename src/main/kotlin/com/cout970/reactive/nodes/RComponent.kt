package com.cout970.reactive.nodes

import com.cout970.reactive.core.*
import org.liquidengine.legui.component.Component
import kotlin.reflect.KClass

class RComponentDescriptor<P : RProps, T : RComponent<P, *>>(val clazz: Class<T>, val props: P) : RDescriptor {
    override fun mapToComponent(): Component = throw IllegalStateException("This descriptor needs a special treatment!")
}

fun <P : RProps, T : RComponent<P, *>> RBuilder.child(clazz: KClass<T>, props: P) =
        child(clazz.java, props)

fun <P : RProps, T : RComponent<P, *>> RBuilder.child(clazz: Class<T>, props: P) =
        +RNode("RComp", RComponentDescriptor(clazz, props))

