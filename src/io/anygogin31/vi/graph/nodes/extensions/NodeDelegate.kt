package io.anygogin31.vi.graph.nodes.extensions

import io.anygogin31.vi.graph.ExecutionResult
import io.anygogin31.vi.graph.Graph
import io.anygogin31.vi.graph.nodes.Node
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private typealias NodeDelegate<Input, Output> = ReadOnlyProperty<Any?, Node<Input, Output>>

public fun <Input, Output> Graph<*>.node(
    name: String? = null,
    execute: suspend (input: Input) -> ExecutionResult<Output>,
): NodeDelegate<Input, Output> =
    NodeDelegate { _, property: KProperty<*> ->
        object : Node<Input, Output>() {
            public override val name: CharSequence =
                (name ?: property.name) +
                    NAME_SEPARATOR +
                    this@node.name

            public override suspend fun execute(input: Input): ExecutionResult<Output> =
                execute.invoke(
                    input,
                )
        }
    }
