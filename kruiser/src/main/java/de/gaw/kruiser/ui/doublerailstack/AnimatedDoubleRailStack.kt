package de.gaw.kruiser.ui.doublerailstack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.state.NavigationState

/**
 * A composable to render always the most recent two screens side by side.
 * aka some fancy behavior.
 */
@Composable
fun AnimatedDoubleRailStack(
    modifier: Modifier = Modifier,
    state: NavigationState = LocalNavigationState.current,
) {
    // TODO
}