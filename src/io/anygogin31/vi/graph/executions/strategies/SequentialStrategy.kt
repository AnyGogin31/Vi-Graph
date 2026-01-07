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

package io.anygogin31.vi.graph.executions.strategies

import io.anygogin31.vi.graph.Graph
import io.anygogin31.vi.graph.exceptions.NodeExecutionException
import io.anygogin31.vi.graph.executions.ExecutionResult
import io.anygogin31.vi.graph.nodes.Node
import io.anygogin31.vi.graph.nodes.extensions.ResolvedEdge
import io.anygogin31.vi.graph.nodes.extensions.resolveEdgesUnsafe

public abstract class SequentialStrategy internal constructor() {
    internal suspend fun <Input> Graph<*>.executeSequential(input: Input): ExecutionResult<*> {
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
                    .resolveEdgesUnsafe(nodeOutput)
                    .firstOrNull()
                    ?: break

            currentNode = resolvedEdge.edge.nodeTo
            currentInput = resolvedEdge.output
        }

        return ExecutionResult.success(currentInput)
    }
}
