package de.gaw.kruiser.backstack

import de.gaw.kruiser.destination.Destination
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

typealias ImmutableEntries = ImmutableList<Destination>
typealias BackstackEntries = List<Destination>

interface Backstack {
    val entries: StateFlow<BackstackEntries>
}

interface MutableBackstack : Backstack {
    fun mutate(block: BackstackEntries.() -> BackstackEntries)
}