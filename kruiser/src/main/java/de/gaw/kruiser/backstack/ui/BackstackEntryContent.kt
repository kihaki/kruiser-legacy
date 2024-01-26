package de.gaw.kruiser.backstack.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import de.gaw.kruiser.backstack.BackstackEntry
import de.gaw.kruiser.backstack.ui.transition.orchestrator.rememberScreen
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.viewmodel.viewModelStoreOwner

val LocalBackstackEntry =
    compositionLocalOf<BackstackEntry> { error("No Local BackstackEntry provided.") }

@Composable
fun BackstackEntry.Render(
    stateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
) {
    CompositionLocalProvider(LocalBackstackEntry provides this) {
        stateHolder.SaveableStateProvider(id) {
            val backstack = LocalBackstack.currentOrThrow
            val entryViewModelStoreOwner = backstack.viewModelStoreOwner(this)
            CompositionLocalProvider(LocalViewModelStoreOwner provides entryViewModelStoreOwner) {
                val screen = rememberScreen()
                screen.Content()
            }
        }
    }
}