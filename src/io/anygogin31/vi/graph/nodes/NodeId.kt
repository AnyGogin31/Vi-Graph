package io.anygogin31.vi.graph.nodes

import kotlin.jvm.JvmInline

@JvmInline
public value class NodeId private constructor(
    public val value: CharSequence,
) {
    public companion object {
        private const val PREFIX: String = "@node"

        private const val SEPARATOR: Char = '$'

        public operator fun invoke(name: CharSequence): NodeId = NodeId(PREFIX + SEPARATOR + name)
    }
}
