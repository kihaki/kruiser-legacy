package de.gaw.kruiser.state.preview

import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreviewNavigationState(
    destinations: List<Destination> = emptyList(),
) : NavigationState {
    override val stack: StateFlow<List<Destination>> = MutableStateFlow(destinations)

    override fun mutate(block: List<Destination>.() -> List<Destination>) {
        // No op
    }
}