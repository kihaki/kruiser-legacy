package de.gaw.kruiser.backstack.core

import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun MutableBackstack(
    initial: BackstackEntries = emptyList(),
    id: String = MutableBackstack.generateId(),
): MutableBackstack = MutableBackstackImpl(
    initial = initial.toImmutableList(),
    id = id,
)

interface MutableBackstack : Backstack {
    fun mutate(block: BackstackEntries.() -> BackstackEntries)

    companion object
}

private class MutableBackstackImpl(
    initial: ImmutableEntries,
    override val id: String,
) : MutableBackstack {
    override val entries = MutableStateFlow(initial)

    override fun mutate(block: BackstackEntries.() -> BackstackEntries) {
        entries.update {
            block(it).toPersistentList()
        }
    }
}

fun MutableBackstack.Companion.generateId() = Backstack.generateId()