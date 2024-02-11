package de.gaw.kruiser.backstack.core

import de.gaw.kruiser.destination.Destination
import java.io.Serializable
import java.util.UUID

data class BackstackEntry(
    val destination: Destination,
    val id: String = generateId(),
) : Serializable {
    companion object
}

fun BackstackEntry.Companion.generateId() = UUID.randomUUID().toString()