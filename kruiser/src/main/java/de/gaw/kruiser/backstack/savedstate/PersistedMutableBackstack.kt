package de.gaw.kruiser.backstack.savedstate

import androidx.lifecycle.SavedStateHandle
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.BackstackId
import de.gaw.kruiser.backstack.core.MutableBackstack
import de.gaw.kruiser.destination.Destination

@Suppress("FunctionName")
fun SavedStateHandle.PersistedMutableBackstack(
    id: BackstackId,
    initial: List<Destination>,
): MutableBackstack = SavedStateMutableBackstack(
    savedStateHandle = this,
    parent = get<ParceledBackstack>(backstackSavedStateId(id))
        ?.asMutableBackstack() // If saved Backstack exists, restore it
        ?: MutableBackstack(id = id, initial = initial), // If no saved Backstack, create a new one
)

private class SavedStateMutableBackstack(
    private val savedStateHandle: SavedStateHandle,
    private val parent: MutableBackstack,
) : MutableBackstack by parent {

    private val savedStateKey: String by lazy { backstackSavedStateId(parent.id) }

    init {
        persist()
    }

    override fun mutate(block: BackstackEntries.() -> BackstackEntries) {
        parent.mutate(block)
        persist()
    }

    private fun persist() {
        savedStateHandle[savedStateKey] = parent.asSaveable()
    }
}

private fun backstackSavedStateId(id: String) = "backstack:$id"

