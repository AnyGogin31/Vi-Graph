package io.anygogin31.vi.graph

import io.anygogin31.vi.graph.nodes.Node
import io.anygogin31.vi.graph.nodes.extensions.ResolvedEdge
import io.anygogin31.vi.graph.nodes.extensions.resolveEdgeUnsafe
import io.anygogin31.vi.graph.nodes.nodeStartOf

public interface Graph<Input> {
    public val name: CharSequence

    public val nodeStart: Node<Input, Input>

    public suspend fun <Input> execute(
        input: Input,
        strategy: ExecutionStrategy = ExecutionStrategy.Sequential,
    ): ExecutionResult<*>
}

public fun <Input> graph(name: String): Graph<Input> =
    object : Graph<Input> {
        public override val name: CharSequence = name

        public override val nodeStart: Node<Input, Input> = nodeStartOf()

        public override suspend fun <Input> execute(
            input: Input,
            strategy: ExecutionStrategy,
        ): ExecutionResult<*> =
            when (strategy) {
                is ExecutionStrategy.Sequential -> executeSequential(input)
            }

        private suspend fun <Input> executeSequential(input: Input): ExecutionResult<*> {
            var currentNode: Node<*, *> = nodeStart
            var currentInput: Any? = input

            while (currentInput != null) {
                val nodeOutput: Any? =
                    currentNode
                        .executeUnsafe(currentInput)
                        .getOrElse { exception: Throwable ->
                            return ExecutionResult.failure<Any?>(exception)
                        }

                val resolvedEdge: ResolvedEdge =
                    currentNode
                        .resolveEdgeUnsafe(nodeOutput)
                        ?: break

                currentNode = resolvedEdge.edge.nodeTo
                currentInput = resolvedEdge.output
            }

            return ExecutionResult.success(currentInput)
        }
    }
