package io.anygogin31.vi.graph.nodes

import io.anygogin31.vi.graph.ExecutionResult
import io.anygogin31.vi.graph.edges.Edge
import io.anygogin31.vi.graph.edges.extensions.EdgeList

public abstract class Node<Input, Output> internal constructor() {
    public abstract val name: CharSequence

    public val id: NodeId
        get() = NodeId.invoke(name)

    public val edges: EdgeList<Output, *>
        field = mutableListOf()

    public open fun addEdge(edge: Edge<Output, *>) {
        edges.add(
            element = edge,
        )
    }

    public abstract suspend fun execute(input: Input): ExecutionResult<Output>

    @Suppress("UNCHECKED_CAST")
    public suspend fun executeUnsafe(input: Any?): ExecutionResult<Output> =
        execute(
            input = input as Input,
        )

    protected companion object {
        public const val NAME_SEPARATOR = ':'
    }
}
