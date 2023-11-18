package de.gaw.kruiser.backstack.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.Entries
import de.gaw.kruiser.backstack.ImmutableEntries
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private class DerivedBackstack(
    scope: CoroutineScope,
    parent: Backstack,
    mapping: Entries.() -> Entries,
) : Backstack {
    override val entries: StateFlow<ImmutableEntries> =
        parent.entries.map { mapping(it).toPersistentList() }
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = parent.entries.value.mapping().toPersistentList()
            )
}

@Composable
fun derivedBackstackOf(backstack: Backstack, mapping: Entries.() -> Entries): Backstack {
    val scope = rememberCoroutineScope()
    val currentMapping by rememberUpdatedState(mapping)
    return remember(scope, backstack) {
        DerivedBackstack(scope, backstack, currentMapping)
    }
}