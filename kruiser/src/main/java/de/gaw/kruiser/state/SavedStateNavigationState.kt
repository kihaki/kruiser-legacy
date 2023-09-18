package de.gaw.kruiser.state

import androidx.lifecycle.SavedStateHandle
import de.gaw.kruiser.destination.Destination

class SavedStateNavigationState(
    private val navStateKey: String,
    private val savedStateHandle: SavedStateHandle,
    initialStack: List<Destination> = emptyList(),
) : NavigationState {
    override val stack = savedStateHandle.getStateFlow(navStateKey, initialStack)

    override fun mutate(block: List<Destination>.() -> List<Destination>) {
        savedStateHandle[navStateKey] = block(stack.value)
    }
}