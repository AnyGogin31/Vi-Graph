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
            Result.success(
                value = input,
            )
    }
