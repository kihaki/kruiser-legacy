package de.gaw.kruiser.backstack.core

import de.gaw.kruiser.destination.Destination
import java.io.Serializable
import java.util.UUID

/**
 * Identifies an entry on the [Backstack].
 * Each entry has a unique identifier and a destination.
 * The unique identifier is used to distinguish between the same [BackstackEntry] at different
 * positions on the same [Backstack].
 *
 * @param destination The destination of the entry.
 * @param id The unique identifier of the entry. If not provided, a random id will be generated.
 */
data class BackstackEntry(
    val destination: Destination,
    val id: String = generateId(),
) : Serializable {
    companion object
}

fun BackstackEntry.Companion.generateId() = UUID.randomUUID().toString()