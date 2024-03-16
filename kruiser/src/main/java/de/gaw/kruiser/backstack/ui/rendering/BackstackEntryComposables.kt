package de.gaw.kruiser.backstack.ui.rendering

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.ui.util.LocalBackstackState
import de.gaw.kruiser.backstack.ui.util.LocalSaveableStateHolder
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.backstack.util.rememberScreen
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.viewmodel.viewModelStoreOwner

val LocalBackstackEntry = compositionLocalOf<BackstackEntry?> { null }

@Composable
fun BackstackEntry.Render(
    screen: Screen = rememberScreen(),
    stateHolder: SaveableStateHolder = LocalSaveableStateHolder.currentOrThrow,
    content: @Composable (Screen) -> Unit = { it.Content() },
) {
    CompositionLocalProvider(LocalBackstackEntry provides this) {
        stateHolder.SaveableStateProvider(this) {
            val backstack = LocalBackstackState.currentOrThrow
            val entryViewModelStoreOwner = backstack.viewModelStoreOwner(this)
            CompositionLocalProvider(LocalViewModelStoreOwner provides entryViewModelStoreOwner) {
                content(screen)
            }
        }
    }
}