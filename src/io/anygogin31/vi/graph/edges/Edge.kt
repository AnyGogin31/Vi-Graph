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

package io.anygogin31.vi.graph.edges

import io.anygogin31.vi.graph.nodes.Node

public class Edge<Source, Target>
    private constructor(
        public val nodeFrom: Node<*, Source>,
        public val nodeTo: Node<Target, *>,
    ) {
        internal var condition: suspend (source: Source) -> Boolean =
            { true }
            private set

        public infix fun onCondition(block: suspend (source: Source) -> Boolean): Edge<Source, Target> =
            apply {
                condition = block
            }

        public companion object {
            public infix fun <Source, Target> Node<*, Source>.forwardTo(to: Node<Target, *>): Edge<Source, Target> =
                Edge(
                    nodeFrom = this,
                    nodeTo = to,
                )
        }
    }
