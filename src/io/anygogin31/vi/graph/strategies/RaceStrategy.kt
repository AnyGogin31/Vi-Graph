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
import io.anygogin31.vi.graph.strategies.internal.GraphStackFrame
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.getOrElse
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

public class RaceStrategy(
    private val coroutineDispatcher: CoroutineDispatcher =
        Dispatchers.Default,
) : ExecutionStrategy {
    public override suspend fun <Input> Graph<Input>.execute(
        input: Input,
        nodeStart: Node<Input, Input>,
    ): Result<Any?> =
        coroutineScope {
            val resultChannel: Channel<Result<Any?>> =
                Channel(
                    capacity = Channel.RENDEZVOUS,
                )

            val processJob: Job =
                launch(coroutineDispatcher) {
                    processNode(
                        input = input,
                        nodeStart = nodeStart,
                        resultChannel = resultChannel,
                    )
                }

            val result: Result<Any?> =
                resultChannel
                    .receiveCatching()
                    .getOrElse { exception: Throwable? ->
                        return@coroutineScope Result.failure(
                            GraphExecutionException(
                                name = name,
                                cause = exception,
                            ),
                        )
                    }

            processJob.cancelAndJoin()
            resultChannel.close()

            return@coroutineScope result
        }

    private suspend fun processNode(
        input: Any?,
        nodeStart: Node<*, *>,
        resultChannel: Channel<Result<Any?>>,
    ) {
        val stack: ArrayDeque<GraphStackFrame> = ArrayDeque()
        stack.addLast(
            GraphStackFrame(
                node = nodeStart,
                input = input,
            ),
        )

        while (stack.isNotEmpty()) {
            val graphStackFrame: GraphStackFrame = stack.removeLast()
            val currentNode: Node<*, *> = graphStackFrame.node
            val currentInput: Any? = graphStackFrame.input

            val nodeOutput: Any? =
                currentNode
                    .executeUnsafe(currentInput)
                    .getOrElse { exception: Throwable ->
                        resultChannel.trySend(
                            Result.failure(
                                NodeExecutionException(
                                    name = currentNode.name,
                                    cause = exception,
                                ),
                            ),
                        )
                        return
                    }

            currentNode
                .resolveEdgesUnsafe(nodeOutput)
                .ifEmpty {
                    resultChannel.trySend(
                        Result.success(
                            value = nodeOutput,
                        ),
                    )
                    return
                }
                .forEach { resolvedEdge: ResolvedEdge ->
                    stack.addLast(
                        GraphStackFrame(
                            node = resolvedEdge.edge.nodeTo,
                            input = resolvedEdge.output,
                        ),
                    )
                }
        }
    }
}
