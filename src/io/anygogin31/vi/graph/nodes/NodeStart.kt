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
import io.anygogin31.vi.graph.Graph

private const val START_NODE_PREFIX: String = "__start__"

internal fun <Input> Graph<Input>.nodeStartOf(): Node<Input, Input> =
    object : Node<Input, Input>() {
        public override val name: CharSequence =
            START_NODE_PREFIX +
                NAME_SEPARATOR +
                this@nodeStartOf.name

        public override suspend fun execute(input: Input): ExecutionResult<Input> =
            ExecutionResult.success(
                value = input,
            )
    }
