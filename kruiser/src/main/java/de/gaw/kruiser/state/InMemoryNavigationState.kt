package de.gaw.kruiser.state

import de.gaw.kruiser.destination.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class InMemoryNavigationState(
    initialStack: List<Destination> = emptyList(),
) : NavigationState {
    override val stack = MutableStateFlow(initialStack)

    override fun mutate(block: List<Destination>.() -> List<Destination>) =
        stack.update(block)
}

