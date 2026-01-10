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

import io.anygogin31.vi.graph.edges.Edge
import io.anygogin31.vi.graph.exceptions.GraphConfigurationException
import io.anygogin31.vi.graph.nodes.Node
import io.anygogin31.vi.graph.strategies.ExecutionStrategy
import io.anygogin31.vi.graph.strategies.JoinStrategy

public interface Pipeline<Input, Output> : Graph<Input> {
    public override suspend fun execute(
        input: Input,
        strategy: ExecutionStrategy,
    ): Result<*>
}

public open class PipelineBuilder<Input, Output>
    internal constructor() : GraphBuilder<Input>() {
        public val nodeFinish: Node<Output, Output> =
            object : Node<Output, Output>() {
                public override val name: CharSequence = FINISH_NODE_PREFIX

                public override suspend fun execute(input: Output): Result<Output> =
                    Result.success(
                        value = input,
                    )

                internal override fun addEdge(edge: Edge<Output, *>): Nothing =
                    throw GraphConfigurationException(
                        message = "${this.name} cannot have outgoing edges",
                    )
            }

        private companion object {
            private const val FINISH_NODE_PREFIX: String = "__finish__"
        }
    }

public fun <Input, Output> pipeline(
    name: CharSequence,
    block: PipelineBuilder<Input, Output>.() -> Unit = {},
): Pipeline<Input, Output> {
    val pipelineBuilder: PipelineBuilder<Input, Output> =
        PipelineBuilder<Input, Output>()
            .apply(block)
    val graphDelegate: Graph<Input> =
        graph(
            name = name,
            block = {},
        )
    return object : Pipeline<Input, Output>, Graph<Input> by graphDelegate {
        @Suppress("UNCHECKED_CAST")
        public override suspend fun execute(
            input: Input,
            strategy: ExecutionStrategy,
        ): Result<Output> =
            when (strategy) {
                is JoinStrategy ->
                    strategy.run {
                        execute(
                            input = input,
                            nodeStart = pipelineBuilder.nodeStart,
                            nodeFinish = pipelineBuilder.nodeFinish,
                        )
                    }

                else ->
                    graphDelegate.execute(
                        input = input,
                        strategy = strategy,
                    )
            }.map { it as Output }
    }
}
