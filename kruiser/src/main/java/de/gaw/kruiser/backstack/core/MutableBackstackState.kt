package de.gaw.kruiser.backstack.core

import de.gaw.kruiser.destination.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * A [BackstackState] that can be mutated via [mutate].
 */
interface MutableBackstackState : BackstackState {
    /**
     * Mutates the current entries using the given [block].
     * The [block] is called with the current entries and should return the new entries.
     */
    fun mutate(block: BackstackEntries.() -> BackstackEntries)

    companion object
}

/**
 * Creates a new [MutableBackstackState] with the given [content] entries and [id].
 */
@JvmName("MutableBackstackStateFromDestinations")
fun MutableBackstackState(
    id: BackstackStateId,
    content: List<Destination> = emptyList(),
): MutableBackstackState = MutableBackstackState(
    id = id,
    content = content.map(::BackstackEntry),
)

/**
 * Creates a new [MutableBackstackState] with the given [content] entries and [id].
 */
@JvmName("MutableBackstackStateFromEntries")
fun MutableBackstackState(
    id: BackstackStateId,
    content: BackstackEntries = emptyList(),
): MutableBackstackState = MutableBackstackStateImpl(
    id = id,
    content = content,
)

private class MutableBackstackStateImpl(
    override val id: BackstackStateId,
    content: BackstackEntries,
) : MutableBackstackState {

    override val entries = MutableStateFlow(content)

    override fun mutate(block: BackstackEntries.() -> BackstackEntries) =
        entries.update(block)
}