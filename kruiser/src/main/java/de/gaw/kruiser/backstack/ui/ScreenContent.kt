package de.gaw.kruiser.backstack.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.LocalSaveableStateHolder
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.viewmodel.destinationViewModelStoreOwner

/**
 * Provides the context for a [Destination], such as a ViewModelStore and SaveStateProvider.
 */
@Composable
fun ScreenContent(
    destination: Destination,
    savedStateKey: Any = destination,
    stateHolder: SaveableStateHolder = LocalSaveableStateHolder.current,
    backstack: Backstack = LocalBackstack.current,
) {
    stateHolder.SaveableStateProvider(savedStateKey) {
        val viewModelStoreOwner = destinationViewModelStoreOwner(
            destination = destination,
            canDispose = { !backstack.entries.value.contains(it) },
        )

        CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
            val screen = remember(destination) { destination.build() }
            screen.Content()
        }
    }
}