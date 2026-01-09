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

package io.anygogin31.vi.graph.strategies

import io.anygogin31.vi.graph.Graph
import io.anygogin31.vi.graph.exceptions.NodeExecutionException
import io.anygogin31.vi.graph.nodes.Node
import io.anygogin31.vi.graph.nodes.extensions.ResolvedEdge
import io.anygogin31.vi.graph.nodes.extensions.resolveEdgesUnsafe

public class SequentialStrategy : ExecutionStrategy {
    internal suspend fun <Input> Graph<*>.execute(
        input: Input,
        nodeStart: Node<Input, Input>,
    ): Result<*> {
        var currentNode: Node<*, *> = nodeStart
        var currentInput: Any? = input

        while (true) {
            val nodeOutput: Any? =
                currentNode
                    .executeUnsafe(currentInput)
                    .getOrElse { exception: Throwable ->
                        return Result.failure<Any?>(
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
                    ?: return Result.success(
                        value = nodeOutput,
                    )

            currentNode = resolvedEdge.edge.nodeTo
            currentInput = resolvedEdge.output
        }
    }
}
