package com.cout970.reactive.core

import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.ScrollBar
import org.liquidengine.legui.component.ScrollablePanel

interface IMergeStrategy {

    fun merge(old: Component, new: Component): Component
}

object DefaultMergeStrategy : IMergeStrategy {

    override fun merge(old: Component, new: Component): Component {

        // Move childs to the new tree to be checked and updated by ReconciliationManager#traverse
        new.clearChildComponents()
        new.addAll(old.childComponents)

        // Move old components to the new tree to keep their state
        old.metadata[Renderer.METADATA_COMPONENTS]?.let { compStates ->
            new.metadata[Renderer.METADATA_COMPONENTS] = compStates
        }

        return new
    }
}

object ScrollablePanelMergeStrategy : IMergeStrategy {

    override fun merge(old: Component, new: Component): Component {
        old as ScrollablePanel
        new as ScrollablePanel

        DefaultMergeStrategy.merge(old, new)

        old.animation.stopAnimation()

        return new
    }
}

object ScrollBarMergeStrategy : IMergeStrategy {

    override fun merge(old: Component, new: Component): Component {
        old as ScrollBar
        new as ScrollBar

        if (new === old) return old

        DefaultMergeStrategy.merge(old, new)

        new.curValue = old.curValue
        new.visibleAmount = old.visibleAmount

        old.animation.stopAnimation()

        return new
    }
}