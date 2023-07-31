package de.gaw.kruiser.state

import androidx.lifecycle.SavedStateHandle
import de.gaw.kruiser.destination.Destination

class SavedStateNavigationState(
    private val navStateKey: String,
    private val eventStateKey: String,
    private val savedStateHandle: SavedStateHandle,
    initialStack: List<Destination> = emptyList(),
    initialEvent: NavigationState.Event = NavigationState.Event.Idle,
) : NavigationState {
    override val stack = savedStateHandle.getStateFlow(navStateKey, initialStack)
    override var lastEvent = savedStateHandle.getStateFlow(eventStateKey, initialEvent)

    override fun mutate(block: MutableList<Destination>.() -> Unit) {
        savedStateHandle[navStateKey] = stack.value
            .toMutableList()
            .let { mutableStack ->
                val listBeforeSize = mutableStack.size
                val beforeTopDestination = mutableStack.lastOrNull()

                block(mutableStack)

                val listAfterSize = mutableStack.size
                val afterTopDestination = mutableStack.lastOrNull()

                savedStateHandle[eventStateKey] = when {
                    listBeforeSize < listAfterSize -> NavigationState.Event.Push
                    listBeforeSize > listAfterSize -> NavigationState.Event.Pop
                    beforeTopDestination != afterTopDestination -> NavigationState.Event.Replace
                    else -> NavigationState.Event.Idle
                }

                mutableStack
            }
    }
}