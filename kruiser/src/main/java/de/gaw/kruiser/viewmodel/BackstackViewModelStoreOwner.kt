package de.gaw.kruiser.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.BackstackEntry
import de.gaw.kruiser.backstack.currentEntries
import de.gaw.kruiser.destination.Destination

/**
 * Holds the [ViewModelStoreOwner]s for [Destination]s, allowing [ViewModel]s to be scoped to
 * [BackstackEntry]s.
 */
private object BackstackViewModelStoreOwner {
    private val viewModelStoreOwners: MutableMap<BackstackEntry, ViewModelStoreOwner> = mutableMapOf()

    fun remove(entry: BackstackEntry) = viewModelStoreOwners.remove(entry)

    operator fun get(entry: BackstackEntry): ViewModelStoreOwner {
        return when (val owner = viewModelStoreOwners[entry]) {
            null -> object : ViewModelStoreOwner {
                override val viewModelStore: ViewModelStore = ViewModelStore()
            }.also {
                viewModelStoreOwners[entry] = it
            }

            else -> owner
        }
    }
}

/**
 * Creates a [ViewModelStoreOwner] for the current [BackstackEntry]
 *
 * @param entry: The [BackstackEntry] to scope the [ViewModelStoreOwner] to.
 * @param disposeWhen: Checks whether the [ViewModel]s should be disposed, can be called whenever the system requires it.
 * This enables arbitrary scoping of [ViewModel]s, for Android Default behavior see the implementation of [Backstack.viewModelStoreOwner].
 */
@Composable
fun backstackEntryViewModelStoreOwner(
    entry: BackstackEntry,
    disposeWhen: (BackstackEntry) -> Boolean,
): ViewModelStoreOwner {
    val backstackEntryViewModelStoreOwner = BackstackViewModelStoreOwner[entry]

    val currentCanDispose by rememberUpdatedState(disposeWhen)
    DisposableEffect(entry, backstackEntryViewModelStoreOwner) {
        onDispose {
            if (currentCanDispose(entry)) {
                backstackEntryViewModelStoreOwner.viewModelStore.clear()
                BackstackViewModelStoreOwner.remove(entry)
            }
        }
    }

    return backstackEntryViewModelStoreOwner
}

/**
 * Creates a [ViewModelStoreOwner] for the current [BackstackEntry] using Android Default behavior
 * of keeping the [ViewModel] around for as long as the [entry] is on the [Backstack].
 *
 * @param entry: The [BackstackEntry] to scope the [ViewModelStoreOwner] to.
 */
@Composable
fun Backstack.viewModelStoreOwner(entry: BackstackEntry): ViewModelStoreOwner {
    val currentBackstack by rememberUpdatedState(this)
    return backstackEntryViewModelStoreOwner(
        entry = entry,
        disposeWhen = { !currentBackstack.currentEntries().contains(it) },
    )
}