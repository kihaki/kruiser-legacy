package de.gaw.kruiser.state

import de.gaw.kruiser.destination.Destination
import kotlinx.coroutines.flow.StateFlow

interface NavigationState {
    enum class Event {
        Idle, Push, Pop, Replace
    }

    val stack: StateFlow<List<Destination>>
    val lastEvent: StateFlow<Event>
    fun mutate(block: List<Destination>.() -> List<Destination>)
}
