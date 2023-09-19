package de.gaw.kruiser.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.collectIsEmpty
import de.gaw.kruiser.state.pop

/**
 * Will pop the current screen off of the navigation stack when the back action is detected.
 * aka default Android behavior.
 */
@Composable
fun PopOnBackHandler(
    state: NavigationState = LocalNavigationState.current,
) {
    val isEmpty by state.collectIsEmpty()

    BackHandler(
        enabled = !isEmpty,
        onBack = state::pop,
    )
}