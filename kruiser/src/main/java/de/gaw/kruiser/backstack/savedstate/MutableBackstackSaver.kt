package de.gaw.kruiser.backstack.savedstate

import androidx.compose.runtime.saveable.Saver
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.core.MutableBackstackState

fun mutableBackstackStateSaver(): Saver<MutableBackstackState, ParcelableBackstackState> =
    MutableBackstackStateSaver

private val MutableBackstackStateSaver = Saver<MutableBackstackState, ParcelableBackstackState>(
    save = { original -> original.asSaveable() },
    restore = { saved -> saved.asMutableBackstackState() }
)

internal fun BackstackState.asSaveable() = ParcelableBackstackState(
    id = id,
    entries = entries.value.toList(),
)

internal fun ParcelableBackstackState.asMutableBackstackState() = MutableBackstackState(
    id = id,
    content = entries,
)
