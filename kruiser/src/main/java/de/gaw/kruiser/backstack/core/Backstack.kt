package de.gaw.kruiser.backstack.core

import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

interface Backstack {
    val id: BackstackId
    val entries: StateFlow<BackstackEntries>

    companion object
}

typealias BackstackId = String