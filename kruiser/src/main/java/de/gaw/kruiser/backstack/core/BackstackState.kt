package de.gaw.kruiser.backstack.core

import kotlinx.coroutines.flow.StateFlow

interface BackstackState {
    val id: BackstackStateId
    val entries: StateFlow<BackstackEntries>

    companion object
}

typealias BackstackStateId = String