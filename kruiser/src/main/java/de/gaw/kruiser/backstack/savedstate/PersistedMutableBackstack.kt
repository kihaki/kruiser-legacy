package de.gaw.kruiser.backstack.savedstate

import androidx.lifecycle.SavedStateHandle
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.core.BackstackStateId
import de.gaw.kruiser.backstack.core.MutableBackstackState
import de.gaw.kruiser.destination.Destination

/**
 * Creates a [MutableBackstackState] that is persisted automatically in the [SavedStateHandle]
 * after every mutation.
 */
@Suppress("FunctionName")
fun SavedStateHandle.PersistedMutableBackstack(
    id: BackstackStateId,
    initial: List<Destination>,
): MutableBackstackState = SavedStateMutableBackstack(
    handle = this,
    wrapped = restoreBackstackState(id) ?: MutableBackstackState(id = id, content = initial),
)

/**
 * Restores a [MutableBackstackState] from the [SavedStateHandle].
 */
fun SavedStateHandle.restoreBackstackState(id: BackstackStateId) =
    get<ParcelableBackstackState>(backstackSavedStateId(id))?.asMutableBackstackState()

/**
 * Saves the given [state] to the [SavedStateHandle].
 */
fun SavedStateHandle.save(state: BackstackState) {
    this[backstackSavedStateId(state.id)] = state.asSaveable()
}

private class SavedStateMutableBackstack(
    private val handle: SavedStateHandle,
    private val wrapped: MutableBackstackState,
) : MutableBackstackState by wrapped {
    override fun mutate(block: BackstackEntries.() -> BackstackEntries) {
        wrapped.mutate(block)
        handle.save(wrapped)
    }
}

private fun backstackSavedStateId(id: BackstackStateId) = "backstack:$id"

