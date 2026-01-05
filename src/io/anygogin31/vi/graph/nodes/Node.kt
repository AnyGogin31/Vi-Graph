package io.anygogin31.vi.graph.nodes

import io.anygogin31.vi.graph.edges.Edge
import io.anygogin31.vi.graph.edges.extensions.EdgeList

public sealed class Node<Input, Output> protected constructor(
    public val name: String,
) {
    public val id: NodeId
        get() = NodeId.invoke(name)

    public val edges: EdgeList<Output, *>
        field = mutableListOf()

    public open fun addEdge(edge: Edge<Output, *>) {
        edges.add(
            element = edge,
        )
    }

    public abstract suspend fun execute(input: Input): Result<Output>

    @Suppress("UNCHECKED_CAST")
    public suspend fun executeUnsafe(input: Any?): Result<Output> =
        execute(
            input = input as Input,
        )
}

public class StartNode<Input> internal constructor(
    graphName: String,
) : Node<Input, Input>(PREFIX + SEPARATOR + graphName) {
    public override suspend fun execute(input: Input): Result<Input> =
        Result.success(
            value = input,
        )

    private companion object {
        private const val PREFIX: String = "__start__"

        private const val SEPARATOR: Char = ':'
    }
}

public class FinishNode<Output> internal constructor(
    graphName: String,
) : Node<Output, Output>(PREFIX + SEPARATOR + graphName) {
    public override suspend fun execute(input: Output): Result<Output> =
        Result.failure(
            exception = IllegalStateException("Reached terminal node $name"),
        )

    public override fun addEdge(edge: Edge<Output, *>): Unit =
        error(
            message = "${this::class.simpleName} cannot have outgoing edges",
        )

    private companion object {
        private const val PREFIX: String = "__finish__"

        private const val SEPARATOR: Char = ':'
    }
}
