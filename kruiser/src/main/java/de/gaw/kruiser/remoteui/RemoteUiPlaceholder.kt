package de.gaw.kruiser.remoteui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.transition.LocalExitTransitionTracker
import de.gaw.kruiser.transition.collectCurrentExitTransition

/**
 * Should be added to where the remote ui should render to act as a placeholder.
 *
 * This placeholder will render at the same size as the matching [RemoteUi].
 * The matching [RemoteUi] will render somewhere else at the same position as this placeholder.
 */
@Composable
fun RemoteUiPlaceholder(
    key: RemoteUiKey,
    isRemoteUiPositionSource: (stack: List<Destination>) -> Boolean,
) {
    Box(
        modifier = Modifier
            .applyRemoteUiSize(key)
            .updateRemoteUiOffset(key, isRemoteUiPositionSource)
    ) {
        UpdateRemoteUiVisibilityEffect(key)
    }
}

/**
 * Updates the visibility of the corresponding RemoteUi based on the placeholders visibility.
 */
@Composable
private fun UpdateRemoteUiVisibilityEffect(key: RemoteUiKey) {
    val remoteUiCoordinator = LocalRemoteUiCoordinator.current
    DisposableEffect(remoteUiCoordinator, key) {
        remoteUiCoordinator.updateVisibility(key, true)
        onDispose {
            remoteUiCoordinator.updateVisibility(key, false)
        }
    }
}

/**
 * Takes the size of the actual RemoteUi and applies it to the placeholder 
 * (the RemoteUi decides its size, the placeholder decides its position).
 */
private fun Modifier.applyRemoteUiSize(key: RemoteUiKey) = composed {
    val remoteUiCoordinator = LocalRemoteUiCoordinator.current
    val remoteUiLayout by remoteUiCoordinator.collectLayout(key)
    val size by remember { derivedStateOf { remoteUiLayout.size } }
    size(size)
}

/**
 * Updates the RemoteUis offset based on the placeholders offset.
 * Only updates the offset when the [updateIf] condition is met.
 * // TODO: Attach the remote Ui to a destination and let someone else decide which offset to use
 */
private fun Modifier.updateRemoteUiOffset(
    key: RemoteUiKey,
    updateIf: (stack: List<Destination>) -> Boolean,
) = composed {
    val remoteUiCoordinator = LocalRemoteUiCoordinator.current
    val navigationState = LocalNavigationState.current
    val stack by navigationState.collectCurrentStack()
    val exitTransitionTracker = LocalExitTransitionTracker.current
    val exitTransition by exitTransitionTracker.collectCurrentExitTransition()
    val exitingDestination by remember(exitTransitionTracker) {
        derivedStateOf { exitTransition?.destination }
    }
    val isSource by remember {
        derivedStateOf { updateIf((stack + exitingDestination).filterNotNull()) }
    }

    if (isSource) {
        onGloballyPositioned { coordinates ->
            remoteUiCoordinator.updatePosition(key, coordinates.localToRoot(Offset.Zero))
        }
    } else {
        this
    }
}