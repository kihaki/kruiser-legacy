package de.gaw.kruiser.backstack

import de.gaw.kruiser.destination.Destination
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

typealias ImmutableEntries = ImmutableList<Destination>
typealias Entries = List<Destination>

interface Backstack {
    val entries: StateFlow<ImmutableEntries>
}

interface MutableBackstack : Backstack {
    fun mutate(block: Entries.() -> Entries)
}

