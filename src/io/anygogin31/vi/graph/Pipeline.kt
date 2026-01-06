package io.anygogin31.vi.graph

import io.anygogin31.vi.graph.nodes.Node
import io.anygogin31.vi.graph.nodes.nodeFinishOf

public interface Pipeline<Input, Output : Any> : Graph<Input> {
    public val nodeFinish: Node<Output, Output>

    public override suspend fun <Input> execute(
        input: Input,
        strategy: ExecutionStrategy,
    ): ExecutionResult<Output>
}

public fun <Input, Output : Any> pipline(name: String): Pipeline<Input, Output> {
    val graphDelegate: Graph<Input> = graph(name)
    return object : Pipeline<Input, Output>, Graph<Input> by graphDelegate {
        public override val name: CharSequence = name

        public override val nodeFinish: Node<Output, Output> = nodeFinishOf()

        @Suppress("UNCHECKED_CAST")
        public override suspend fun <Input> execute(
            input: Input,
            strategy: ExecutionStrategy,
        ): ExecutionResult<Output> =
            graphDelegate
                .execute(
                    input = input,
                    strategy = strategy,
                ).map { it as Output }
    }
}
