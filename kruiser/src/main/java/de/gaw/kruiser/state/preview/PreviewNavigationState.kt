package de.gaw.kruiser.state.preview

import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.NavigationState.Event
import de.gaw.kruiser.state.NavigationState.Event.Idle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreviewNavigationState(
    destinations: List<Destination> = emptyList(),
    lastEvent: Event = Idle,
    ) : NavigationState {
    override val stack: StateFlow<List<Destination>> = MutableStateFlow(destinations)
    override val lastEvent: StateFlow<Event> = MutableStateFlow(lastEvent)

    override fun mutate(block: List<Destination>.() -> List<Destination>) {
        // No op
    }
}