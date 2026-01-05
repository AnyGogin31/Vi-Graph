package io.anygogin31.vi.graph

import io.anygogin31.vi.graph.nodes.NodeFinish

public class Pipeline<Input, Output>(
    public override val name: String,
) : Graph<Input>(name) {
    public val nodeFinish: NodeFinish<Output> =
        NodeFinish(
            graphName = name,
        )

    @Suppress("UNCHECKED_CAST")
    public override suspend fun execute(input: Input): Output? {
        val result: Any? = super.execute(input)
        return result as? Output
    }
}
