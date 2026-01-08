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

import io.anygogin31.vi.graph.executions.ExecutionResult
import io.anygogin31.vi.graph.executions.ExecutionStrategy
import io.anygogin31.vi.graph.nodes.Node
import io.anygogin31.vi.graph.nodes.nodeStartOf

public interface Graph<Input> {
    public val name: CharSequence

    public val nodeStart: Node<Input, Input>

    public suspend fun execute(
        input: Input,
        strategy: ExecutionStrategy = ExecutionStrategy.Sequential,
    ): ExecutionResult<*>
}

public fun <Input> graph(
    name: String,
    block: Graph<Input>.() -> Unit = {},
): Graph<Input> =
    object : Graph<Input> {
        public override val name: CharSequence = "@graph:$name"

        public override val nodeStart: Node<Input, Input> = nodeStartOf()

        public override suspend fun execute(
            input: Input,
            strategy: ExecutionStrategy,
        ): ExecutionResult<*> =
            when (strategy) {
                is ExecutionStrategy.Parallel ->
                    strategy.run {
                        executeParallel(
                            input = input,
                            dispatcher = strategy.dispatcher,
                        )
                    }

                is ExecutionStrategy.Sequential ->
                    strategy.run {
                        executeSequential(
                            input = input,
                        )
                    }
            }
    }.apply(block)
