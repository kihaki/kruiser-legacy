package de.gaw.kruiser.backstack.core

import de.gaw.kruiser.destination.Destination
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * A [Backstack] that can be mutated via [mutate].
 */
interface MutableBackstack : Backstack {
    fun mutate(block: BackstackEntries.() -> BackstackEntries)

    companion object
}

/**
 * Creates a new [MutableBackstack] with the given [initial] entries and [id].
 */
fun MutableBackstack(
    id: BackstackId,
    initial: List<Destination> = emptyList(),
): MutableBackstack = MutableBackstackImpl(
    initial = initial.map(::BackstackEntry),
    id = id,
)

/**
 * Creates a new [MutableBackstack] with the given [initial] entries and [id].
 */
fun MutableBackstack(
    id: BackstackId,
    initial: BackstackEntries = emptyList(),
): MutableBackstack = MutableBackstackImpl(
    initial = initial.toImmutableList(),
    id = id,
)

private class MutableBackstackImpl(
    initial: BackstackEntries,
    override val id: BackstackId,
) : MutableBackstack {
    override val entries = MutableStateFlow(initial)

    override fun mutate(block: BackstackEntries.() -> BackstackEntries) {
        entries.update {
            block(it).toPersistentList()
        }
    }
}