package de.gaw.kruiser.viewmodel

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.core.BackstackStateId
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.backstack.util.currentEntries
import de.gaw.kruiser.destination.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

/**
 * Holds the [ViewModelStoreOwner]s for [Destination]s, allowing [ViewModel]s to be scoped to
 * [BackstackEntry]s.
 */
class BackstackViewModelStoreOwners {
    private val viewModelStoreOwners: MutableMap<BackstackEntry, ViewModelStoreOwner> =
        mutableMapOf()

    /**
     * Entries that are currently on screen and should not be disposed of (transitioning out).
     */
    val onScreenEntries = MutableStateFlow(emptySet<BackstackEntry>())

    fun disposeIfNotOnScreen(entries: BackstackEntries) {
        val toDispose = (entries - onScreenEntries.value).toSet()
        toDispose.forEach { entry ->
            Log.v("DestinationTracking", "Disposing ViewModels for ${entry.destination}")
            viewModelStoreOwners.remove(entry)?.viewModelStore?.clear()
        }
    }

    fun disposeIfNotOnScreen(entry: BackstackEntry) {
        disposeIfNotOnScreen(listOf(entry))
    }

    operator fun get(entry: BackstackEntry): ViewModelStoreOwner =
        viewModelStoreOwners.getOrPut(entry) {
            Log.v("DestinationTracking", "Creating ViewModelStoreOwner for ${entry.destination}")
            DefaultViewModelStoreOwner()
        }

    /**
     * Disposes of all [ViewModel]s by force.
     * Used for clearing the store when the [Activity] is finished for example.
     */
    fun clear() {
        onScreenEntries.update { emptySet() }
        viewModelStoreOwners.forEach { (entry, viewModelStoreOwner) ->
            Log.v("DestinationTracking", "Disposing ViewModels for ${entry.destination}")
            viewModelStoreOwner.viewModelStore.clear()
        }
        viewModelStoreOwners.clear()
    }

    private class DefaultViewModelStoreOwner : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore by lazy { ViewModelStore() }
    }
}

internal val backstackStateViewModelStoreOwners =
    mutableMapOf<BackstackStateId, BackstackViewModelStoreOwners>()

val LocalBackstackEntryViewModelStoreOwners =
    compositionLocalOf<BackstackViewModelStoreOwners?> { null }

@Composable
fun DisposeViewModelsEffect(backstackState: BackstackState) {
    val storeOwner = LocalBackstackEntryViewModelStoreOwners.currentOrThrow

    // This will dispose of the ViewModels if the matching backstackEntry was removed while not being rendered itself
    LaunchedEffect(backstackState, storeOwner) {
        var previousEntries = backstackState.currentEntries()
        backstackState.entries.collectLatest { currentEntries ->
            storeOwner.disposeIfNotOnScreen(previousEntries - currentEntries)
            previousEntries = currentEntries
        }
    }

    // This will dispose of the ViewModels if the activity is finished
    DisposableEffectIgnoringConfiguration(backstackState, storeOwner) {
        onDispose {
            backstackStateViewModelStoreOwners.remove(backstackState.id)?.clear()
        }
    }
}

/**
 * Creates a [ViewModelStoreOwner] for the current [BackstackEntry] using Android Default behavior
 * of keeping the [ViewModel] around for as long as the [entry] is on the [BackstackState].
 *
 * @param entry: The [BackstackEntry] to scope the [ViewModelStoreOwner] to.
 */
@Composable
fun BackstackState.viewModelStoreOwner(entry: BackstackEntry): ViewModelStoreOwner {
    val storeOwner = LocalBackstackEntryViewModelStoreOwners.currentOrThrow
    val backstackEntryViewModelStoreOwner = storeOwner.get(entry)

    // This will dispose of the ViewModel after transitions are done
    DisposableEffect(this, storeOwner) {
        storeOwner.onScreenEntries.update { it + entry }
        onDispose {
            storeOwner.onScreenEntries.update { it - entry }
            if (!currentEntries().contains(entry)) {
                storeOwner.disposeIfNotOnScreen(entry)
            }
        }
    }

    return backstackEntryViewModelStoreOwner
}