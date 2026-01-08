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
import io.anygogin31.vi.graph.nodes.nodeFinishOf

public interface Pipeline<Input, Output : Any> : Graph<Input> {
    public val nodeFinish: Node<Output, Output>

    public override suspend fun execute(
        input: Input,
        strategy: ExecutionStrategy,
    ): ExecutionResult<Output>
}

public fun <Input, Output : Any> pipeline(
    name: String,
    block: Pipeline<Input, Output>.() -> Unit = {},
): Pipeline<Input, Output> {
    val graphDelegate: Graph<Input> = graph(name)
    return object : Pipeline<Input, Output>, Graph<Input> by graphDelegate {
        public override val name: CharSequence = "@pipeline:$name"

        public override val nodeFinish: Node<Output, Output> = nodeFinishOf()

        @Suppress("UNCHECKED_CAST")
        public override suspend fun execute(
            input: Input,
            strategy: ExecutionStrategy,
        ): ExecutionResult<Output> =
            graphDelegate
                .execute(
                    input = input,
                    strategy = strategy,
                ).map { it as Output }
    }.apply(block)
}
