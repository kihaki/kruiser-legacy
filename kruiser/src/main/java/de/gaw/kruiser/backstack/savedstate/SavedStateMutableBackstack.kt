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
fun SavedStateMutableBackstack(
    handle: SavedStateHandle,
    id: BackstackStateId,
    initial: List<Destination> = emptyList(),
): MutableBackstackState = SavedStateMutableBackstack(
    handle = handle,
    wrapped = handle.restoreBackstackState(id)
        ?: MutableBackstackState(id = id, content = initial),
)

/**
 * Restores a [MutableBackstackState] from the [SavedStateHandle].
 */
fun SavedStateHandle.restoreBackstackState(id: BackstackStateId) =
    get<ParcelableBackstackState>(id.toSavedStateId())?.asMutableBackstackState()

/**
 * Saves the given [state] to the [SavedStateHandle].
 */
fun SavedStateHandle.save(state: BackstackState) {
    this[state.id.toSavedStateId()] = state.asSaveable()
}

private class SavedStateMutableBackstack(
    private val handle: SavedStateHandle,
    private val wrapped: MutableBackstackState,
) : MutableBackstackState by SavedMutableBackstack(
    wrapped = wrapped,
    saver = { state -> handle.save(state) },
)

class SavedMutableBackstack(
    private val wrapped: MutableBackstackState,
    private val saver: (MutableBackstackState) -> Unit,
) : MutableBackstackState by wrapped {
    override fun mutate(block: BackstackEntries.() -> BackstackEntries) {
        wrapped.mutate(block)
        saver(wrapped)
    }
}

private fun BackstackStateId.toSavedStateId() = "backstack:$this"

