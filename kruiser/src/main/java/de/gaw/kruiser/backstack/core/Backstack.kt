package de.gaw.kruiser.backstack.core

import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

interface Backstack {
    val id: String
    val entries: StateFlow<BackstackEntries>

    companion object
}

fun Backstack.Companion.generateId() = UUID.randomUUID().toString()