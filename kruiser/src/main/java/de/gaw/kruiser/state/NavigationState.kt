package de.gaw.kruiser.state

import de.gaw.kruiser.destination.Destination
import kotlinx.coroutines.flow.StateFlow

interface NavigationState {
    val stack: StateFlow<List<Destination>>
    fun mutate(block: List<Destination>.() -> List<Destination>)
}
