package de.gaw.kruiser.state

import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState.Event.Idle
import de.gaw.kruiser.state.NavigationState.Event.Pop
import de.gaw.kruiser.state.NavigationState.Event.Push
import de.gaw.kruiser.state.NavigationState.Event.Replace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DefaultNavigationState(
    initialStack: List<Destination> = emptyList(),
) : NavigationState {
    override val stack = MutableStateFlow(initialStack)
    override var lastEvent = MutableStateFlow(Idle)

    override fun mutate(block: MutableList<Destination>.() -> Unit) = stack.update { currentStack ->
        currentStack
            .toMutableList()
            .let { mutableStack ->
                val listBeforeSize = mutableStack.size
                val beforeTopDestination = mutableStack.lastOrNull()

                block(mutableStack)

                val listAfterSize = mutableStack.size
                val afterTopDestination = mutableStack.lastOrNull()

                lastEvent.update {
                    when {
                        listBeforeSize < listAfterSize -> Push
                        listBeforeSize > listAfterSize -> Pop
                        beforeTopDestination != afterTopDestination -> Replace
                        else -> Idle
                    }
                }

                mutableStack
            }
    }
}