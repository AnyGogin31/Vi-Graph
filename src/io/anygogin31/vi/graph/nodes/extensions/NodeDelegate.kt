package io.anygogin31.vi.graph.nodes.extensions

import io.anygogin31.vi.graph.Graph
import io.anygogin31.vi.graph.nodes.Node
import kotlin.reflect.KProperty

public class NodeDelegate<Input, Output>(
    private val name: String,
    private val graphName: String,
    private val execute: suspend (input: Input) -> Output,
) {
    public operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): Node<Input, Output> =
        object : Node<Input, Output>(name + NAME_SEPARATOR + graphName) {
            public override suspend fun execute(input: Input): Result<Output> =
                runCatching {
                    this@NodeDelegate.execute(input)
                }
        }
}

public inline fun <reified Input, reified Output> Graph<*>.node(
    name: String,
    noinline execute: suspend (input: Input) -> Output,
): NodeDelegate<Input, Output> =
    NodeDelegate(
        name = name,
        graphName = this.name,
        execute = execute,
    )
