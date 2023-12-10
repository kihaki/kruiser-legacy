package de.gaw.kruiser.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.currentEntries
import de.gaw.kruiser.destination.Destination

/**
 * Holds the [ViewModelStoreOwner]s for [Destination]s, allowing [ViewModel]s to be scoped to
 * [Destination]s.
 */
private object DestinationsViewModelStoreOwner {
    private val viewModelStoreOwners: MutableMap<Destination, ViewModelStoreOwner> = mutableMapOf()

    fun remove(destination: Destination) = viewModelStoreOwners.remove(destination)

    operator fun get(destination: Destination): ViewModelStoreOwner {
        return when (val owner = viewModelStoreOwners[destination]) {
            null -> object : ViewModelStoreOwner {
                override val viewModelStore: ViewModelStore = ViewModelStore()
            }.also {
                viewModelStoreOwners[destination] = it
            }

            else -> owner
        }
    }
}

/**
 * Creates a [ViewModelStoreOwner] for the current [Destination]
 *
 * @param destination: The [Destination] to scope the [ViewModelStoreOwner] to.
 * @param disposeWhen: Checks whether the [ViewModel]s should be disposed, can be called whenever the system requires it.
 * This enables arbitrary scoping of [ViewModel]s, for Android Default behavior see the implementation of [Backstack.viewModelStoreOwner].
 */
@Composable
fun destinationViewModelStoreOwner(
    destination: Destination,
    disposeWhen: (Destination) -> Boolean,
): ViewModelStoreOwner {
    val destinationViewModelStoreOwner = DestinationsViewModelStoreOwner[destination]

    val currentCanDispose by rememberUpdatedState(disposeWhen)
    DisposableEffect(destination, destinationViewModelStoreOwner) {
        onDispose {
            if (currentCanDispose(destination)) {
                destinationViewModelStoreOwner.viewModelStore.clear()
                DestinationsViewModelStoreOwner.remove(destination)
            }
        }
    }

    return destinationViewModelStoreOwner
}

/**
 * Creates a [ViewModelStoreOwner] for the current [Destination] using Android Default behavior
 * of keeping the [ViewModel] around for as long as the [destination] is on the [Backstack].
 *
 * @param destination: The [Destination] to scope the [ViewModelStoreOwner] to.
 */
@Composable
fun Backstack.viewModelStoreOwner(destination: Destination): ViewModelStoreOwner {
    val currentBackstack by rememberUpdatedState(this)
    return destinationViewModelStoreOwner(
        destination = destination,
        disposeWhen = { !currentBackstack.currentEntries().contains(it) },
    )
}