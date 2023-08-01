package de.gaw.kruiser.screen

import androidx.compose.runtime.Composable
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.currentStack

interface Screen {
    val destination: Destination

    @Composable
    fun Content()

    fun isVisible(navigationState: NavigationState): Boolean =
        navigationState.currentStack.let { destination ->
            destination.lastOrNull() == this.destination || destination.drop(1)
                .lastOrNull() == this.destination // TODO: Make more sophisticated
        }
}