package io.anygogin31.vi.graph

public sealed interface ExecutionStrategy {
    public data object Sequential : ExecutionStrategy
}
