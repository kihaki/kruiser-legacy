package de.gaw.kruiser.backstack

import de.gaw.kruiser.backstack.Backstack.Companion.generateId
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun MutableBackstack(
    initial: BackstackEntries = emptyList(),
    id: String = generateId(),
): MutableBackstack = MutableBackstackImpl(
    initial = initial.toImmutableList(),
    id = id,
)

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