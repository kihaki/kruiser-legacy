package de.gaw.kruiser.transition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import de.gaw.kruiser.state.NavigationState


@Composable
fun ExitAnimationsState.collectOnScreenDestinations() =
    onScreenDestinations.collectAsState(emptyList())
