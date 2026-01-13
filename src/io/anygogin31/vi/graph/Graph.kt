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

import io.anygogin31.vi.graph.exceptions.GraphExecutionException
import io.anygogin31.vi.graph.nodes.Node
import io.anygogin31.vi.graph.strategies.ExecutionStrategy
import io.anygogin31.vi.graph.strategies.JoinStrategy
import io.anygogin31.vi.graph.strategies.RaceStrategy
import io.anygogin31.vi.graph.strategies.SequentialStrategy

public interface Graph<Input> {
    public val name: CharSequence

    public suspend fun execute(
        input: Input,
        strategy: ExecutionStrategy = SequentialStrategy(),
    ): Result<Any?>
}

public open class GraphBuilder<Input>
    internal constructor() {
        public val nodeStart: Node<Input, Input> =
            object : Node<Input, Input>() {
                public override val name: CharSequence = START_NODE_PREFIX

                public override suspend fun execute(input: Input): Result<Input> =
                    Result.success(
                        value = input,
                    )
            }

        private companion object {
            private const val START_NODE_PREFIX: String = "__start__"
        }
    }

public fun <Input> graph(
    name: CharSequence,
    block: GraphBuilder<Input>.() -> Unit = {},
): Graph<Input> {
    val graphBuilder: GraphBuilder<Input> =
        GraphBuilder<Input>()
            .apply(block)
    return graph(
        name = name,
        graphBuilder = graphBuilder,
    )
}

internal fun <Input> graph(
    name: CharSequence,
    graphBuilder: GraphBuilder<Input>,
): Graph<Input> =
    object : Graph<Input> {
        public override val name: CharSequence = name

        public override suspend fun execute(
            input: Input,
            strategy: ExecutionStrategy,
        ): Result<Any?> =
            strategy.run {
                execute(
                    input = input,
                    nodeStart = graphBuilder.nodeStart,
                )
            }
    }
