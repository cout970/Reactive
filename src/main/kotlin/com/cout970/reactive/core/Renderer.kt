package com.cout970.reactive.core

import com.cout970.reactive.nodes.RComponentDescriptor
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.event.Event

object Renderer {

    const val METADATA_KEY = "key"
    const val METADATA_COMPONENTS = "ReactiveRComponents"
    const val METADATA_NODE_TREE = "ReactiveNodeTree"

    // This lock can be used to avoid critical races between threads
    val updateLock = Any()

    fun render(mountPoint: Component, func: RBuilder.() -> Unit): RContext {
        return render(mountPoint, buildNode(func))
    }

    fun render(mountPoint: Component, app: RNode): RContext {
        val ctx = RContext(mountPoint, app)
        updateSubTree(ctx, mountPoint, app)
        return ctx
    }

    fun rerender(ctx: RContext) {
        updateSubTree(ctx, ctx.mountPoint, ctx.app)
    }

    internal fun <S, P> scheduleUpdate(comp: RComponent<P, S>, updateFunc: S.() -> S, setter: (S) -> Unit)
            where S : RState, P : RProps {

        if (!comp.mounted) {
            throw IllegalStateException("Trying to update a unmounted component!")
        }
        AsyncManager.runLater {
            val newState = updateFunc(comp.state)
            if (comp.shouldComponentUpdate(comp.props, newState)) {
                setter(newState)
                val ctx = comp.ctx
                val mount = comp.mountPoint
                comp.componentWillUpdate()
                updateSubTree(ctx, mount, mount.metadata[METADATA_NODE_TREE] as RNode)
            } else {
                setter(newState)
            }
        }
    }

    private fun updateSubTree(ctx: RContext, mount: Component, node: RNode) {
        synchronized(updateLock) {
            preUpdate(ctx)
            unmountAllRComponents(ctx, mount)
            traverse(ctx, mount, node)
            postUpdate(ctx)
        }
    }

    private fun preUpdate(ctx: RContext) {
        ctx.mountedComponents.clear()
        ctx.unmountedComponents.clear()
    }

    private fun postUpdate(ctx: RContext) {
        ctx.mountedComponents.filter { it !in ctx.unmountedComponents }.forEach {
            it.componentWillMount()
            it.componentDidMount()
        }
        ctx.unmountedComponents.filter { it !in ctx.mountedComponents }.forEach {
            it.componentWillUnmount()
        }
    }

    private fun traverse(ctx: RContext, comp: Component, childNodes: RNode) {

        val children: List<Pair<Component, List<RNode>>> = expandLayer(ctx, comp, childNodes)

        comp.metadata[METADATA_NODE_TREE] = childNodes

        if (comp.count() != children.count()) {
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
            comp.clearChilds()
            comp.addAll(newChilds)
        }
    }

    private fun unmountAllRComponents(ctx: RContext, comp: Component) {
        comp.unmountComponents(ctx)
        comp.childs.forEach { unmountAllRComponents(ctx, it) }
    }

    private fun expandLayer(ctx: RContext, comp: Component, node: RNode): List<Pair<Component, List<RNode>>> {

        val descriptor = node.componentDescriptor

        return when (descriptor) {
            is RComponentDescriptor<*, *> -> {
                createRComponent(ctx, descriptor, comp, node.key).render().flatMap { expandLayer(ctx, comp, it) }
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

    @Suppress("UNCHECKED_CAST")
    private fun createRComponent(ctx: RContext, descriptor: RComponentDescriptor<*, *>, mount: Component, key: String?)
            : RComponent<*, *> {
        if (METADATA_COMPONENTS !in mount.metadata) {
            mount.metadata[METADATA_COMPONENTS] = mutableListOf<RComponent<*, *>>()
        }
        val compList = mount.metadata[METADATA_COMPONENTS] as MutableList<RComponent<*, *>>

        val alreadyExisting = compList.find { it::class.java == descriptor.clazz && !it.mounted && it.key == key }
        val rComponent: RComponent<RProps, RState>

        rComponent = if (alreadyExisting != null) {
            alreadyExisting as RComponent<RProps, RState>
        } else {
            try {
                (descriptor.clazz.newInstance() as RComponent<RProps, RState>).also {
                    compList.add(it)
                    it.key = key
                    it.ctx = ctx
                }
            } catch (e: Exception) {
                throw IllegalStateException("${descriptor.clazz} doesn't have a empty constructor!", e)
            }
        }

        rComponent.componentWillReceiveProps(descriptor.props)
        rComponent.mountPoint = mount
        rComponent.mounted = true
        ctx.mountedComponents.add(rComponent)
        return rComponent
    }

    @Suppress("UNCHECKED_CAST")
    private fun createComponent(node: RNode): Component {
        return node.componentDescriptor.mapToComponent().apply {

            metadata[METADATA_KEY] = node.key

            node.listeners.forEach { listener_ ->
                val (clazz, handler) = listener_ as Listener<Event<Component>>
                listenerMap.addListener(clazz, handler)
            }

            node.deferred?.invoke(this)
        }
    }

    private fun merge(ctx: RContext, old: Component, new: Component, childs: List<RNode>): Component {
        if (old.javaClass != new.javaClass) {
            traverse(ctx, new, childs.toFragment())
            return new
        }
        if (old.count() != childs.count()) {
            // Technically childs.count() is not the amount of sub-components what will get generated,
            // because RComponents can generate more than 1 root component
            // I don't know how to handle this situation, so I will use the Ostrich algorithm,
            // just assuming that the trees can't merge and remove all local state in the child components
            traverse(ctx, new, childs.toFragment())
            return new
        }

        // Move childs to the new tree to be checked and updated by traverse
        new.addAll(old.childs)
        // Move old components to the new tree to keep the state
        val compStates = old.metadata[METADATA_COMPONENTS]
        if (compStates != null) {
            new.metadata[METADATA_COMPONENTS] = compStates
        }

        // fuck it my head hurts
        // this was supposed to keep the subtrees that didn't change but fuck it
        // I will fix this later when everything else works
        traverse(ctx, new, childs.toFragment())
        return new
    }

    @Suppress("UNCHECKED_CAST")
    private fun Component.unmountComponents(ctx: RContext) {
        if (METADATA_COMPONENTS in metadata) {
            val list = metadata[METADATA_COMPONENTS] as MutableList<RComponent<*, *>>

            list.filter { it.mounted }.forEach {
                ctx.unmountedComponents.add(it); it.mounted = false
            }
        }
    }

    private fun List<RNode>.toFragment(): RNode {
        return RNode("Fragment", FragmentDescriptor, this)
    }
}