package de.gaw.kruiser.backstack.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.viewmodel.destinationViewModelStoreOwner

@Composable
fun ScreenContent(
    destination: Destination,
    backstack: Backstack = LocalBackstack.current,
) {
    val viewModelStoreOwner =
        destinationViewModelStoreOwner(destination) { !backstack.entries.value.contains(it) }

    val screen = remember { destination.build() }
    CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
        screen.Content()
    }
}