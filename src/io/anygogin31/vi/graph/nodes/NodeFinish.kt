package io.anygogin31.vi.graph.nodes

import io.anygogin31.vi.graph.ExecutionResult
import io.anygogin31.vi.graph.Graph
import io.anygogin31.vi.graph.edges.Edge

private const val FINISH_NODE_PREFIX: String = "__finish__"

internal fun <Output> Graph<*>.nodeFinishOf(): Node<Output, Output> =
    object : Node<Output, Output>() {
        public override val name: CharSequence =
            FINISH_NODE_PREFIX +
                NAME_SEPARATOR +
                this@nodeFinishOf.name

        public override suspend fun execute(input: Output): ExecutionResult<Output> =
            ExecutionResult.failure(
                exception = IllegalStateException("Reached terminal node $name"),
            )

        public override fun addEdge(edge: Edge<Output, *>): Unit =
            error(
                message = "${this::class.simpleName} cannot have outgoing edges",
            )
    }
