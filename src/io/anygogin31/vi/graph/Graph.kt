package io.anygogin31.vi.graph

import io.anygogin31.vi.graph.nodes.Node
import io.anygogin31.vi.graph.nodes.StartNode
import io.anygogin31.vi.graph.nodes.extensions.ResolvedEdge
import io.anygogin31.vi.graph.nodes.extensions.resolveEdgeUnsafe

public open class Graph<Input>(
    public open val name: String,
) {
    public val nodeStart: StartNode<Input> =
        StartNode(
            graphName = name,
        )

    public open suspend fun execute(input: Input): Any? {
        var currentNode: Node<*, *> = nodeStart
        var currentInput: Any? = input

        while (currentInput != null) {
            val nodeOutput: Any? =
                currentNode
                    .executeUnsafe(currentInput)
                    .getOrNull()

            val resolvedEdge: ResolvedEdge =
                currentNode
                    .resolveEdgeUnsafe(nodeOutput)
                    ?: return nodeOutput

            currentNode = resolvedEdge.edge.to
            currentInput = resolvedEdge.output
        }

        return currentInput
    }
}
