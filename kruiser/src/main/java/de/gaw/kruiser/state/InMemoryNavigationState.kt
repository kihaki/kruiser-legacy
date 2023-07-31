package de.gaw.kruiser.state

import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState.Event.Idle
import de.gaw.kruiser.state.NavigationState.Event.Pop
import de.gaw.kruiser.state.NavigationState.Event.Push
import de.gaw.kruiser.state.NavigationState.Event.Replace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class InMemoryNavigationState(
    initialStack: List<Destination> = emptyList(),
) : NavigationState {
    override val stack = MutableStateFlow(initialStack)
    override var lastEvent = MutableStateFlow(Idle)

    override fun mutate(block: List<Destination>.() -> List<Destination>) =
        stack.update { currentStack ->
            val listBeforeSize = currentStack.size
            val beforeTopDestination = currentStack.lastOrNull()

            val updatedStack = block(currentStack)

            val listAfterSize = updatedStack.size
            val afterTopDestination = updatedStack.lastOrNull()

            lastEvent.update {
                when {
                    listBeforeSize < listAfterSize -> Push
                    listBeforeSize > listAfterSize -> Pop
                    beforeTopDestination != afterTopDestination -> Replace
                    else -> Idle
                }
            }

            updatedStack
        }
}

