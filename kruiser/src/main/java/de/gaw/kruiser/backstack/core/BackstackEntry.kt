package de.gaw.kruiser.backstack.core

import android.os.Parcelable
import de.gaw.kruiser.destination.Destination
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Identifies an entry on the [BackstackState].
 * Each entry has a unique identifier and a destination.
 * The unique identifier is used to distinguish between the same [BackstackEntry] at different
 * positions on the same [BackstackState].
 *
 * @param destination The destination of the entry.
 * @param id The unique identifier of the entry. If not provided, a random id will be generated.
 */
@Parcelize
data class BackstackEntry(
    val destination: Destination,
    val id: BackstackEntryId = generateId(),
) : Parcelable {
    companion object
}

fun BackstackEntry.Companion.generateId(): BackstackEntryId = UUID.randomUUID().toString()

typealias BackstackEntryId = String
typealias BackstackEntries = List<BackstackEntry>