package com.cout970.reactive.core

import com.cout970.reactive.nodes.RComponentDescriptor
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.event.Event

object Renderer {

    fun render(mountPoint: Component, func: RBuilder.() -> Unit): RContext {
        return render(mountPoint, buildNode(func))
    }

    fun render(mountPoint: Component, app: RNode): RContext {
        val ctx = RContext(mountPoint, app)
        traverse(ctx, mountPoint, app)
        return ctx
    }

    fun rerender(ctx: RContext) {
        traverse(ctx, ctx.mountPoint, ctx.app)
    }

    internal fun <S : RState, P : RProps> scheduleUpdate(comp: RComponent<P, S>,
                                                         updateFunc: S.() -> S, setter: (S) -> Unit) {
        AsyncManager.runLater {
            val newState = updateFunc(comp.state)
            if (comp.shouldComponentUpdate(comp.props, newState)) {
                setter(newState)
                val ctx = comp.ctx
                val mount = comp.mountPoint
                traverse(ctx, mount, mount.metadata["nodeTree"] as RNode)
            }
        }
    }

    private fun traverse(ctx: RContext, comp: Component, childNodes: RNode) {

        val descriptor = childNodes.componentDescriptor

        val children: List<Pair<Component, List<RNode>>> = when (descriptor) {
            is RComponentDescriptor<*, *> -> {
                createRComponent(ctx, descriptor, comp).render().flatMap { expandLayer(ctx, comp, it) }
            }
            FragmentDescriptor -> {
                childNodes.children.flatMap { expandLayer(ctx, comp, it) }
            }
            EmptyDescriptor -> {
                emptyList()
            }
            else -> {
                expandLayer(ctx, comp, childNodes)
            }
        }

        comp.metadata["nodeTree"] = childNodes

        if (comp.count() != children.count()) {
            // TODO finish this
            comp.unmountComponents()
            comp.clearChilds()
            children.forEach { (childComp, childCompChilds) ->
                comp.add(childComp)
                traverse(ctx, childComp, childCompChilds.toFragment())
            }
        } else {
            val newChilds = comp.childs.zip(children).map { (oldComp, pair) ->
                val (newComp, childs) = pair
                merge(ctx, oldComp, newComp, childs)
            }
            comp.unmountComponents()
            comp.clearChilds()
            comp.addAll(newChilds)
        }
    }

    private fun expandLayer(ctx: RContext, comp: Component, node: RNode): List<Pair<Component, List<RNode>>> {

        val descriptor = node.componentDescriptor

        return when (descriptor) {
            is RComponentDescriptor<*, *> -> {
                createRComponent(ctx, descriptor, comp).render().flatMap { expandLayer(ctx, comp, it) }
            }
            FragmentDescriptor -> {
                node.children.flatMap { expandLayer(ctx, comp, it) }
            }
            EmptyDescriptor -> emptyList()
            else -> {
                listOf(createComponent(node) to node.children)
            }
        }
    }

    private fun createRComponent(ctx: RContext, descriptor: RComponentDescriptor<*, *>, mount: Component)
            : RComponent<*, *> {
        if ("Rcomps" !in mount.metadata) {
            mount.metadata["Rcomps"] = mutableListOf<RComponent<*, *>>()
        }
        val compList = mount.metadata["Rcomps"] as MutableList<RComponent<*, *>>

        val rcomp: RComponent<RProps, RState> = (compList.find { it::class.java == descriptor.clazz && !it.mounted }
                                                 ?: descriptor.clazz.newInstance()) as RComponent<RProps, RState>
        rcomp.componentWillReceiveProps(descriptor.props)
        rcomp.ctx = ctx
        rcomp.mountPoint = mount
        compList.add(rcomp)
        rcomp.mounted = true
        rcomp.componentWillMount()
        return rcomp
    }

    @Suppress("UNCHECKED_CAST")
    private fun createComponent(node: RNode): Component {
        val newComp = node.componentDescriptor.mapToComponent()
        newComp.metadata["key"] = node.key
        node.listeners.forEach { listener_ ->
            val (clazz, handler) = listener_ as Listener<Event<Component>>
            newComp.listenerMap.addListener(clazz, handler)
        }
        node.deferred?.invoke(newComp)
        return newComp
    }

    private fun merge(ctx: RContext, old: Component, new: Component, childs: List<RNode>): Component {
        if (old.javaClass != new.javaClass) {
            traverse(ctx, new, childs.toFragment())
            return new
        }
        if (old.count() != childs.count()) {
            // I don't know how to handle this situation, so I will use the Ostrich algorithm,
            // just rerender everything hopping the performance impact is not too big
            traverse(ctx, new, childs.toFragment())
            return new
        } else {
            if (old.childs.any { !it.metadata.containsKey("key") }) {
                println("Warning ${old.childs.find { !it.metadata.containsKey("key") }} doesn't have a key")

                // no keys to help optimize
                traverse(ctx, new, childs.toFragment())
                return new
            }

            // fuck it my head hurts
            // this was supposed to keep the subtrees that didn't change but fuck it
            // I will fix this later when everything else works
            traverse(ctx, new, childs.toFragment())
            return new
        }
    }

    private fun Component.unmountComponents() {
        if ("Rcomps" in metadata) {
            val list = metadata["Rcomps"] as MutableList<RComponent<*, *>>
            list.forEach { it.componentWillUnmount(); it.mounted = false }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun RComponentDescriptor<*, *>.create(): RComponent<*, *> {
        val inst = clazz.newInstance() as RComponent<RProps, RState>
        inst.componentWillReceiveProps(props)
        return inst
    }

    private fun List<RNode>.toFragment(): RNode {
        return RNode("Fragment", FragmentDescriptor, this)
    }
}