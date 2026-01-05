package io.anygogin31.vi.graph.nodes

import io.anygogin31.vi.graph.edges.Edge

public class NodeFinish<Output> internal constructor(
    graphName: String,
) : Node<Output, Output>(PREFIX + NAME_SEPARATOR + graphName) {
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
    }
}
