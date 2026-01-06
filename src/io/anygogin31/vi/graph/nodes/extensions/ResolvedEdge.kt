package io.anygogin31.vi.graph.nodes.extensions

import io.anygogin31.vi.graph.edges.Edge
import io.anygogin31.vi.graph.nodes.Node

public data class ResolvedEdge(
    public val edge: Edge<*, *>,
    public val output: Any?,
)

public suspend fun <Input, Output> Node<Input, Output>.resolveEdge(output: Output): ResolvedEdge? =
    edges
        .find { edge: Edge<Output, *> -> edge.condition.invoke(output) }
        ?.let { edge: Edge<Output, *> ->
            ResolvedEdge(
                edge = edge,
                output =
                    edge
                        .transform
                        .invoke(output)
                        .getOrNull(),
            )
        }

@Suppress("UNCHECKED_CAST")
public suspend fun <Input, Output> Node<Input, Output>.resolveEdgeUnsafe(output: Any?): ResolvedEdge? =
    resolveEdge(
        output = output as Output,
    )
