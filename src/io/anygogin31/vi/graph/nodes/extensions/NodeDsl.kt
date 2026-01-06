package io.anygogin31.vi.graph.nodes.extensions

import io.anygogin31.vi.graph.Graph
import io.anygogin31.vi.graph.edges.Edge

public fun <Source, Target> Graph<*>.edge(edge: Edge<Source, Target>) =
    edge
        .nodeFrom
        .addEdge(edge)
