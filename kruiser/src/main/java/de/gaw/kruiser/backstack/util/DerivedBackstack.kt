package de.gaw.kruiser.backstack.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.core.BackstackEntries
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

/**
 * Returns a [BackstackState] that was mapped via [mapping]
 *
 * @param backstack: [BackstackState] to map.
 * @param mapping: the mapping to apply to all emissions of [BackstackState]
 */
@Composable
fun rememberDerivedBackstack(
    backstack: BackstackState,
    mapping: BackstackEntries.() -> BackstackEntries,
): BackstackState {
    val scope = rememberCoroutineScope()
    val currentMapping by rememberUpdatedState(mapping)
    val derivedBackstackId = rememberSaveable(backstack.id) {
        "${backstack.id}-${UUID.randomUUID()}"
    }
    return remember(scope, backstack.id) {
        DerivedBackstack(
            scope = scope,
            id = derivedBackstackId,
            parent = backstack,
            mapping = currentMapping,
        )
    }
}

private class DerivedBackstack(
    scope: CoroutineScope,
    parent: BackstackState,
    override val id: String,
    mapping: BackstackEntries.() -> BackstackEntries,
) : BackstackState {
    override val entries: StateFlow<BackstackEntries> =
        parent.entries
            .map(mapping)
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = parent.entries.value.mapping().toPersistentList()
            )
}