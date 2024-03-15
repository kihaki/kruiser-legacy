package de.gaw.kruiser.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.util.currentEntries
import de.gaw.kruiser.destination.Destination

/**
 * Holds the [ViewModelStoreOwner]s for [Destination]s, allowing [ViewModel]s to be scoped to
 * [BackstackEntry]s.
 */
private object BackstackViewModelStoreOwner {
    private val viewModelStoreOwners: MutableMap<BackstackEntry, ViewModelStoreOwner> =
        mutableMapOf()

    fun remove(entry: BackstackEntry) = viewModelStoreOwners.remove(entry)

    operator fun get(entry: BackstackEntry): ViewModelStoreOwner =
        viewModelStoreOwners.getOrPut(entry) {
            object : ViewModelStoreOwner {
                override val viewModelStore: ViewModelStore = ViewModelStore()
            }
        }
}

/**
 * Creates a [ViewModelStoreOwner] for the current [BackstackEntry]
 *
 * @param entry: The [BackstackEntry] to scope the [ViewModelStoreOwner] to.
 * @param disposeWhen: Checks whether the [ViewModel]s should be disposed, can be called whenever the system requires it.
 * This enables arbitrary scoping of [ViewModel]s, for Android Default behavior see the implementation of [BackstackState.viewModelStoreOwner].
 */
@Composable
fun backstackEntryViewModelStoreOwner(
    entry: BackstackEntry,
    disposeWhen: (BackstackEntry) -> Boolean,
): ViewModelStoreOwner {
    val backstackEntryViewModelStoreOwner = BackstackViewModelStoreOwner[entry]

    DisposableEffect(backstackEntryViewModelStoreOwner) {
        onDispose {
            if (disposeWhen(entry)) {
                backstackEntryViewModelStoreOwner.viewModelStore.clear()
                BackstackViewModelStoreOwner.remove(entry)
            }
        }
    }

    return backstackEntryViewModelStoreOwner
}

/**
 * Creates a [ViewModelStoreOwner] for the current [BackstackEntry] using Android Default behavior
 * of keeping the [ViewModel] around for as long as the [entry] is on the [BackstackState].
 *
 * @param entry: The [BackstackEntry] to scope the [ViewModelStoreOwner] to.
 */
@Composable
fun BackstackState.viewModelStoreOwner(entry: BackstackEntry): ViewModelStoreOwner {
    return backstackEntryViewModelStoreOwner(
        entry = entry,
        disposeWhen = { !currentEntries().contains(it) },
    )
}