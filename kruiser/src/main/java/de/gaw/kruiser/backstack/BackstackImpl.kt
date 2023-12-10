package de.gaw.kruiser.backstack

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun MutableBackstack(initial: BackstackEntries = emptyList()): MutableBackstack =
    MutableBackstackImpl(initial.toImmutableList())

private class MutableBackstackImpl(
    initial: ImmutableEntries = persistentListOf(),
) : MutableBackstack {
    override val entries = MutableStateFlow(initial)

    override fun mutate(block: BackstackEntries.() -> BackstackEntries) {
        entries.update {
            block(it).toPersistentList()
        }
    }
}