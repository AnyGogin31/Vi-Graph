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

package io.anygogin31.vi.graph.nodes

import io.anygogin31.vi.graph.Graph
import io.anygogin31.vi.graph.edges.Edge
import io.anygogin31.vi.graph.exceptions.GraphConfigurationException
import io.anygogin31.vi.graph.executions.ExecutionResult

private const val FINISH_NODE_PREFIX: String = "__finish__"

internal fun <Output> Graph<*>.nodeFinishOf(): Node<Output, Output> =
    object : Node<Output, Output>() {
        public override val name: CharSequence =
            FINISH_NODE_PREFIX +
                NAME_SEPARATOR +
                this@nodeFinishOf.name

        public override suspend fun execute(input: Output): ExecutionResult<Output> =
            ExecutionResult.success(
                value = input,
            )

        public override fun addEdge(edge: Edge<Output, *>): Unit =
            throw GraphConfigurationException(
                message = "${this::class.simpleName} ($name) cannot have outgoing edges",
            )
    }
