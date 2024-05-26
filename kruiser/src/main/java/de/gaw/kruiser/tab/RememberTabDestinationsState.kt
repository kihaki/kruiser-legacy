package de.gaw.kruiser.tab

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.destination.Destination
import kotlinx.parcelize.Parcelize

@Composable
fun rememberTabDestinationsState(
    destinations: List<Destination>,
    initial: Destination = destinations.first(),
): TabDestinationsState = rememberSaveable(
    saver = TabDestinationsStateSaver,
) { TabDestinationsState(destinations, initial) }

@Parcelize
private data class TabDestinationsStateSnapshot(
    val current: BackstackEntry,
    val entries: BackstackEntries,
) : Parcelable

private val TabDestinationsStateSaver = Saver<TabDestinationsState, TabDestinationsStateSnapshot>(
    save = { state ->
        TabDestinationsStateSnapshot(
            current = state.current.value,
            entries = state.entries.value,
        )
    },
    restore = { saved ->
        TabDestinationsState(
            entries = saved.entries,
            initial = saved.current,
        )
    },
)