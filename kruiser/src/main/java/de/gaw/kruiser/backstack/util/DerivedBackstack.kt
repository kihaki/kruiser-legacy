package de.gaw.kruiser.backstack.util

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import de.gaw.kruiser.backstack.core.Backstack
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.ImmutableEntries
import de.gaw.kruiser.backstack.core.generateId
import de.gaw.kruiser.backstack.currentEntries
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private class DerivedBackstack(
    scope: CoroutineScope,
    parent: Backstack,
    override val id: String,
    mapping: BackstackEntries.() -> BackstackEntries,
) : Backstack {
    override val entries: StateFlow<BackstackEntries> =
        parent.entries
            .map(mapping)
            .map(BackstackEntries::toPersistentList)
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = parent.entries.value.mapping().toPersistentList()
            )
}

/**
 * Returns a [Backstack] that was mapped via [mapping]
 *
 * @param backstack: [Backstack] to map.
 * @param mapping: the mapping to apply to all emissions of [Backstack]
 */
@Composable
fun rememberDerivedBackstackOf(
    backstack: Backstack,
    mapping: BackstackEntries.() -> BackstackEntries,
): Backstack {
    val scope = rememberCoroutineScope()
    val currentMapping by rememberUpdatedState(mapping)
    val derivedBackstackId = rememberSaveable(backstack.id) {
        "${backstack.id}::${Backstack.generateId()}"
    }
    return remember(scope, backstack.id) {
        Log.v("Kruiser", "Deriving new Backstack for ${backstack.id} with id $derivedBackstackId")
        DerivedBackstack(
            scope = scope,
            id = derivedBackstackId,
            parent = backstack,
            mapping = currentMapping,
        )
    }
}

/**
 * Keeps the previous backstack state of the provided [Backstack].
 * This is useful to determine if there was a push or pop action for example.
 *
 * @param backstack: [Backstack] to keep the previous state of
 */
@Composable
fun rememberPreviousBackstackOf(backstack: Backstack): Backstack {
    var cachedEntries by remember { mutableStateOf(backstack.currentEntries()) }
    return rememberDerivedBackstackOf(backstack) {
        val previousEntries = cachedEntries
        cachedEntries = this
        previousEntries
    }
}