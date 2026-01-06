package io.anygogin31.vi.graph.edges

import io.anygogin31.vi.graph.ExecutionResult
import io.anygogin31.vi.graph.nodes.Node

private typealias NodeFrom<Source> = Node<*, Source>
private typealias NodeTo<Source, Target> = Node<Source, Target>

public class Edge<Source, Target> private constructor(
    public val nodeFrom: NodeFrom<Source>,
    public val nodeTo: NodeTo<Source, Target>,
) {
    public var condition: suspend (source: Source) -> Boolean =
        { true }
        private set

    public infix fun onCondition(block: suspend (source: Source) -> Boolean): Edge<Source, Target> =
        this.apply {
            condition = block
        }

    public var transform: suspend (source: Source) -> ExecutionResult<Target> =
        { nodeTo.execute(it) }
        private set

    public infix fun transformed(block: suspend (source: Source) -> ExecutionResult<Target>): Edge<Source, Target> =
        this.apply {
            transform = block
        }

    public companion object {
        public infix fun <Source, Target> NodeFrom<Source>.forwardTo(to: NodeTo<Source, Target>): Edge<Source, Target> =
            Edge(
                nodeFrom = this,
                nodeTo = to,
            )
    }
}
