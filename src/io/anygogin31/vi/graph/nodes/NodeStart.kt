package io.anygogin31.vi.graph.nodes

public class NodeStart<Input> internal constructor(
    graphName: String,
) : Node<Input, Input>(PREFIX + NAME_SEPARATOR + graphName) {
    public override suspend fun execute(input: Input): Result<Input> =
        Result.success(
            value = input,
        )

    private companion object {
        private const val PREFIX: String = "__start__"
    }
}
