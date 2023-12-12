package de.gaw.kruiser.backstack.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.BackstackEntry
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.LocalSaveableStateHolder
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.viewmodel.viewModelStoreOwner

/**
 * Provides the context for a [Destination], such as a ViewModelStore and SaveStateProvider.
 */
@Composable
fun ScreenContent(
    backstackEntry: BackstackEntry,
    stateHolder: SaveableStateHolder = LocalSaveableStateHolder.current,
    backstack: Backstack = LocalBackstack.current,
) {
    stateHolder.SaveableStateProvider(backstackEntry.id) {
        val screen = remember(backstackEntry) { backstackEntry.destination.build() }
        val destinationViewModelStoreOwner = backstack.viewModelStoreOwner(backstackEntry)

        CompositionLocalProvider(LocalViewModelStoreOwner provides destinationViewModelStoreOwner) {
            screen.Content()
        }
    }
}