package de.gaw.kruiser.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import de.gaw.kruiser.destination.Destination

object DestinationsViewModelStoreOwner {
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
 * Creates a ViewModelStoreOwner for the current destination
 *
 * @param destination: The [Destination] to scope the ViewModelStoreOwner to
 * @param
 */
@Composable
fun destinationViewModelStoreOwner(
    destination: Destination,
    canDispose: (Destination) -> Boolean,
): ViewModelStoreOwner {
    val destinationViewModelStoreOwner = DestinationsViewModelStoreOwner[destination]

    val currentCanDispose by rememberUpdatedState(canDispose)
    DisposableEffect(destination) {
        onDispose {
            if (currentCanDispose(destination)) {
                destinationViewModelStoreOwner.viewModelStore.clear()
                DestinationsViewModelStoreOwner.remove(destination)
            }
        }
    }

    return destinationViewModelStoreOwner
}