package de.gaw.kruiser.tab

import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.core.BackstackEntryId
import de.gaw.kruiser.backstack.core.generateId
import de.gaw.kruiser.destination.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

interface TabDestinationsState {
    val current: StateFlow<BackstackEntry>
    val entries: StateFlow<BackstackEntries>

    /**
     * Set the current tab.
     * @param block A block that receives the [TabDestinationsState]'s entries, the current tab,
     * and returns the new current tab.
     */
    fun setCurrent(block: (candidates: BackstackEntries, current: BackstackEntry) -> BackstackEntry)
}

fun TabDestinationsState(
    entries: BackstackEntries,
    initial: BackstackEntry = entries.first(),
): TabDestinationsState = DefaultTabDestinationsState(
    entries = entries,
    initial = initial,
)

fun TabDestinationsState(
    destinations: List<Destination>,
    initial: Destination = destinations.first(),
    generateId: (Destination) -> BackstackEntryId = { BackstackEntry.generateId() },
): TabDestinationsState = DefaultTabDestinationsState(
    destinations = destinations,
    initial = initial,
    generateId = generateId,
)

private class DefaultTabDestinationsState(
    entries: BackstackEntries,
    initial: BackstackEntry = entries.first(),
) : TabDestinationsState {
    constructor(
        destinations: List<Destination>,
        initial: Destination = destinations.first(),
        generateId: (Destination) -> BackstackEntryId = { BackstackEntry.generateId() },
    ) : this(
        entries = destinations.map { BackstackEntry(destination = it, id = generateId(it)) },
        initial = BackstackEntry(destination = initial, id = generateId(initial))
    )

    override val entries = MutableStateFlow(entries)
    override val current = MutableStateFlow(initial)

    override fun setCurrent(
        block: (candidates: BackstackEntries, current: BackstackEntry) -> BackstackEntry,
    ) {
        val candidates = entries.value
        current.update { block(candidates, it) }
    }
}
