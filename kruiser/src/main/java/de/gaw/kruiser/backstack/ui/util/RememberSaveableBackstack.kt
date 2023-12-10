package de.gaw.kruiser.backstack.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import de.gaw.kruiser.backstack.BackstackEntries
import de.gaw.kruiser.backstack.MutableBackstack
import de.gaw.kruiser.destination.Destination

@Composable
fun rememberSaveableBackstack(initial: BackstackEntries = emptyList()) = rememberSaveable(
    initial,
    saver = mutableBackstackSaver(),
) {
    MutableBackstack(initial = initial)
}

@Composable
fun rememberSaveableBackstack(initial: Destination) = rememberSaveableBackstack(listOf(initial))

fun mutableBackstackSaver(): Saver<MutableBackstack, List<Destination>> =
    MutableBackstackSaver

private val MutableBackstackSaver = Saver<MutableBackstack, List<Destination>>(
    save = { it.entries.value.toList() },
    restore = { entries ->
        MutableBackstack(initial = entries)
    }
)