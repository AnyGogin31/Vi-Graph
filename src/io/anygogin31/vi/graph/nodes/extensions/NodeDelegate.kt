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

package io.anygogin31.vi.graph.nodes.extensions

import io.anygogin31.vi.graph.GraphBuilder
import io.anygogin31.vi.graph.nodes.Node
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private class NodeDelegate<Input, Output>(
    name: CharSequence,
    execute: suspend (input: Input) -> Result<Output>,
) : ReadOnlyProperty<Any?, Node<Input, Output>> {
    private val instance: Node<Input, Output> by lazy {
        object : Node<Input, Output>() {
            public override val name: CharSequence = name

            public override suspend fun execute(input: Input): Result<Output> =
                execute.invoke(
                    input,
                )
        }
    }

    public override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): Node<Input, Output> = instance
}

public fun <Input, Output> GraphBuilder<*>.node(
    name: CharSequence,
    execute: suspend (input: Input) -> Result<Output>,
): ReadOnlyProperty<Any?, Node<Input, Output>> =
    NodeDelegate(
        name = name,
        execute = execute,
    )
