/*
 * Vi-Graph
 * Copyright (C) 2026 AnyGogin31
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package io.anygogin31.vi.graph

import io.anygogin31.vi.graph.exceptions.NodeExecutionException
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

public fun <Input> graph(
    name: String,
    block: Graph<Input>.() -> Unit = {},
): Graph<Input> =
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
                            return ExecutionResult.failure<Any?>(
                                NodeExecutionException(
                                    name = currentNode.name,
                                    cause = exception,
                                ),
                            )
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
    }.apply(block)
