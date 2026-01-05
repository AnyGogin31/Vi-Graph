package io.anygogin31.vi.graph.edges

import io.anygogin31.vi.graph.nodes.Node

public class Edge<Source, Target> private constructor(
    public val from: Node<Source, *>,
    public val to: Node<*, Target>,
) {
    public var condition: suspend (source: Source) -> Boolean =
        { true }
        private set

    public infix fun onCondition(block: suspend (source: Source) -> Boolean): Edge<Source, Target> =
        this.apply {
            condition = block
        }

    public var transform: suspend (source: Source) -> Result<Target> =
        { to.executeUnsafe(it) }
        private set

    public infix fun transformed(block: suspend (source: Source) -> Result<Target>): Edge<Source, Target> =
        this.apply {
            transform = block
        }

    public companion object {
        public infix fun <Source, Target> Node<Source, *>.forwardTo(to: Node<*, Target>): Edge<Source, Target> =
            Edge(
                from = this,
                to = to,
            )
    }
}
