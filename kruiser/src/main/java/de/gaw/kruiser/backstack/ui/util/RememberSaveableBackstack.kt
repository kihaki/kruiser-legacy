package de.gaw.kruiser.backstack.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.core.BackstackStateId
import de.gaw.kruiser.backstack.core.MutableBackstackState
import de.gaw.kruiser.backstack.savedstate.mutableBackstackStateSaver
import de.gaw.kruiser.destination.Destination

@Composable
@JvmName("rememberSaveableBackstackFromDestinations")
fun rememberSaveableBackstack(
    backstackId: BackstackStateId,
    initial: List<Destination>,
): MutableBackstackState =
    rememberSaveableBackstack(
        backstackId,
        initial.map { BackstackEntry(it) },
    )

@Composable
fun rememberSaveableBackstack(
    backstackId: BackstackStateId,
    initial: BackstackEntries = emptyList(),
): MutableBackstackState =
    rememberSaveable(
        saver = mutableBackstackStateSaver(),
    ) {
        MutableBackstackState(
            id = backstackId,
            content = initial,
        )
    }

@Composable
@JvmName("rememberSaveableBackstackFromDestination")
fun rememberSaveableBackstack(
    backstackId: BackstackStateId,
    initial: Destination,
): MutableBackstackState =
    rememberSaveableBackstack(
        backstackId,
        listOf(BackstackEntry(initial)),
    )

