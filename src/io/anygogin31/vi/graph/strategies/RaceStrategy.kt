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
import io.anygogin31.vi.graph.exceptions.GraphExecutionException
import io.anygogin31.vi.graph.exceptions.NodeExecutionException
import io.anygogin31.vi.graph.nodes.Node
import io.anygogin31.vi.graph.nodes.extensions.ResolvedEdge
import io.anygogin31.vi.graph.nodes.extensions.resolveEdgesUnsafe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.getOrElse
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

public class RaceStrategy(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ExecutionStrategy {
    private val resultChannel: Channel<Result<*>> =
        Channel(
            capacity = Channel.RENDEZVOUS,
        )

    internal suspend fun <Input> Graph<*>.execute(
        input: Input,
        nodeStart: Node<Input, Input>,
    ): Result<*> =
        coroutineScope {
            launch(dispatcher) {
                processNode(
                    currentNode = nodeStart,
                    currentInput = input,
                )
            }

            resultChannel
                .receiveCatching()
                .getOrElse { exception: Throwable? ->
                    return@coroutineScope Result.failure<Any?>(
                        GraphExecutionException(
                            name = name,
                            cause = exception,
                        ),
                    )
                }.also {
                    coroutineContext.cancelChildren()
                    resultChannel.close()
                }
        }

    private suspend fun processNode(
        currentNode: Node<*, *>,
        currentInput: Any?,
    ): Unit =
        coroutineScope {
            val nodeOutput: Any? =
                currentNode
                    .executeUnsafe(currentInput)
                    .getOrElse { exception: Throwable ->
                        return@coroutineScope resultChannel.send(
                            Result.failure<Any?>(
                                NodeExecutionException(
                                    name = currentNode.name,
                                    cause = exception,
                                ),
                            ),
                        )
                    }

            currentNode
                .resolveEdgesUnsafe(nodeOutput)
                .ifEmpty {
                    return@coroutineScope resultChannel.send(
                        Result.success(
                            value = nodeOutput,
                        ),
                    )
                }
                .forEach { resolvedEdge: ResolvedEdge ->
                    launch(dispatcher) {
                        processNode(
                            currentNode = resolvedEdge.edge.nodeTo,
                            currentInput = resolvedEdge.output,
                        )
                    }
                }
        }
}
