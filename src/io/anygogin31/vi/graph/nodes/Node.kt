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

import io.anygogin31.vi.graph.ExecutionResult
import io.anygogin31.vi.graph.edges.Edge
import io.anygogin31.vi.graph.edges.extensions.EdgeList

public abstract class Node<Input, Output> internal constructor() {
    public abstract val name: CharSequence

    public val id: NodeId
        get() = NodeId.invoke(name)

    public val edges: EdgeList<Output, *>
        field = mutableListOf()

    public open fun addEdge(edge: Edge<Output, *>) {
        edges.add(
            element = edge,
        )
    }

    public abstract suspend fun execute(input: Input): ExecutionResult<Output>

    @Suppress("UNCHECKED_CAST")
    public suspend fun executeUnsafe(input: Any?): ExecutionResult<Output> =
        execute(
            input = input as Input,
        )

    protected companion object {
        public const val NAME_SEPARATOR = '/'
    }
}
