package com.cout970.reactive.core

import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Panel

interface RDescriptor {
    fun mapToComponent(): Component
}

object EmptyDescriptor : RDescriptor {
    override fun mapToComponent(): Component = Panel().apply { isVisible = false; isEnabled = false }
}

object FragmentDescriptor : RDescriptor {
    override fun mapToComponent(): Component = Panel()
}

