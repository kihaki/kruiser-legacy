package de.gaw.kruiser.backstack

import de.gaw.kruiser.destination.Destination
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import java.io.Serializable
import java.util.UUID

typealias ImmutableEntries = ImmutableList<BackstackEntry>
typealias BackstackEntries = List<BackstackEntry>

data class BackstackEntry(
    val destination: Destination,
    val id: String = UUID.randomUUID().toString(),
) : Serializable

interface Backstack {
    val id: String
    val entries: StateFlow<BackstackEntries>

    companion object {
        fun generateId() = UUID.randomUUID().toString()
    }
}

interface MutableBackstack : Backstack {
    fun mutate(block: BackstackEntries.() -> BackstackEntries)
}