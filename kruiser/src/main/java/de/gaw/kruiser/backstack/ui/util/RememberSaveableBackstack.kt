package de.gaw.kruiser.backstack.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.core.BackstackId
import de.gaw.kruiser.backstack.core.MutableBackstack
import de.gaw.kruiser.backstack.savedstate.mutableBackstackSaver
import de.gaw.kruiser.destination.Destination

@Composable
@JvmName("rememberSaveableBackstackFromDestinations")
fun rememberSaveableBackstack(
    backstackId: BackstackId,
    initial: List<Destination>,
): MutableBackstack =
    rememberSaveableBackstack(
        backstackId,
        initial.map { BackstackEntry(it) },
    )

@Composable
fun rememberSaveableBackstack(
    backstackId: BackstackId,
    initial: BackstackEntries = emptyList(),
): MutableBackstack =
    rememberSaveable(
        saver = mutableBackstackSaver(),
    ) {
        MutableBackstack(
            id = backstackId,
            initial = initial,
        )
    }

@Composable
@JvmName("rememberSaveableBackstackFromDestination")
fun rememberSaveableBackstack(
    backstackId: BackstackId,
    initial: Destination,
): MutableBackstack =
    rememberSaveableBackstack(
        backstackId,
        listOf(BackstackEntry(initial)),
    )

