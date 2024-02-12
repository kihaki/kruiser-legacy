package de.gaw.kruiser.backstack.core

import androidx.lifecycle.SavedStateHandle
import de.gaw.kruiser.destination.Destination
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@JvmName("mutableBackstackFromDestinations")
fun MutableBackstack(
    vararg initial: Destination,
    id: String = MutableBackstack.generateId(),
): MutableBackstack = MutableBackstack(
    initial = initial.toList(),
    id = id,
)

@JvmName("mutableBackstackFromDestinationList")
fun MutableBackstack(
    initial: List<Destination> = emptyList(),
    id: String = MutableBackstack.generateId(),
): MutableBackstack = MutableBackstack(
    initial = initial
        .map(::BackstackEntry)
        .toImmutableList(),
    id = id,
)

@JvmName("mutableBackstackFromBackstackEntriesList")
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

fun SavedStateMutableBackstack(
    vararg initial: Destination,
    savedStateHandle: SavedStateHandle,
    scope: CoroutineScope,
    stateKey: String,
): MutableBackstack {
    val savedStateKey = "bs:$stateKey"

    val idKey = "$savedStateKey:backstack-id"
    if (!savedStateHandle.contains(idKey)) {
        savedStateHandle[idKey] = MutableBackstack.generateId()
    }

    val entriesKey = "$savedStateKey:entries"
    if (!savedStateHandle.contains(entriesKey)) {
        savedStateHandle[entriesKey] = initial.map(::BackstackEntry).toList()
    }

    return SavedStateMutableBackstack(
        scope = scope,
        idKey = idKey,
        entriesKey = entriesKey,
        savedStateHandle = savedStateHandle
    )
}

private class SavedStateMutableBackstack(
    scope: CoroutineScope,
    idKey: String,
    private val entriesKey: String,
    private val savedStateHandle: SavedStateHandle,
) : MutableBackstack {

    override fun mutate(block: BackstackEntries.() -> BackstackEntries) {
        savedStateHandle[entriesKey] = block(savedStateHandle[entriesKey] ?: persistentListOf())
    }

    override val id: String = savedStateHandle[idKey]!!
    override val entries = savedStateHandle
        .getStateFlow(
            key = entriesKey,
            initialValue = emptyList<BackstackEntry>(),
        )
        .map(List<BackstackEntry>::toPersistentList)
        .stateIn(scope, SharingStarted.Eagerly, persistentListOf())
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