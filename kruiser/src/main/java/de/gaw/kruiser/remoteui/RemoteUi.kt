package de.gaw.kruiser.remoteui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.transition.LocalExitTransitionTracker
import de.gaw.kruiser.transition.collectCurrentExitTransition

/**
 * Should be added to where the remote ui should render to act as a placeholder.
 * This placeholder will render at the same size as the matching [RemoteUi].
 * The matching [RemoteUi] will render somewhere else at the same
 * position as this placeholder.
 */
@Composable
fun Screen.RemoteUiPlaceholder(
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
 * Will render the remote ui.
 * This will render at the same position as the matching [RemoteUiPlaceholder].
 * The matching [RemoteUiPlaceholder] will render somewhere else at the same
 * size as this placeholder.
 */
@Composable
fun RemoteUi(
    key: RemoteUiKey,
    zIndex: Float = 0f,
    content: @Composable () -> Unit,
) {
    val remoteUiStore = LocalRemoteUiStore.current
    val layout by remoteUiStore.collectLayout(key)
    if (layout.isVisible) {
        Box(
            modifier = Modifier
                .applyRemoteUiOffset(key)
                .updateRemoteUiSize(key)
                .zIndex(zIndex),
        ) {
            content()
        }
    }
}

@Composable
private fun UpdateRemoteUiVisibilityEffect(key: RemoteUiKey) {
    val remoteUiStore = LocalRemoteUiStore.current
    DisposableEffect(remoteUiStore, key) {
        remoteUiStore.updateVisibility(key, true)
        onDispose {
            remoteUiStore.updateVisibility(key, false)
        }
    }
}

@Composable
private fun RemoteUiStore.collectLayout(key: RemoteUiKey) = remember(this, key) {
    derivedStateOf { layouts[key] ?: RemoteUiLayout() }
}

private fun Offset.toIntOffset() = IntOffset(x.toInt(), y.toInt())

private fun Modifier.applyRemoteUiSize(key: RemoteUiKey) = composed {
    val remoteUiStore = LocalRemoteUiStore.current
    val layout by remoteUiStore.collectLayout(key)
    val remoteUiSize by remember { derivedStateOf { layout.size } }
    size(remoteUiSize)
}

private fun Modifier.applyRemoteUiOffset(key: RemoteUiKey) = composed {
    val remoteUiStore = LocalRemoteUiStore.current
    val layout by remoteUiStore.collectLayout(key)
    val remoteUiPosition by remember { derivedStateOf { layout.position.toIntOffset() } }
    offset { remoteUiPosition }
}

private fun Modifier.updateRemoteUiSize(key: RemoteUiKey) = composed {
    val remoteUiStore = LocalRemoteUiStore.current
    val density = LocalDensity.current
    onGloballyPositioned { layoutCoordinates ->
        val size = with(density) {
            with(layoutCoordinates.size) {
                DpSize(width.toDp(), height.toDp())
            }
        }
        remoteUiStore.updateSize(key, size)
    }
}

context(Screen)
private fun Modifier.updateRemoteUiOffset(
    key: RemoteUiKey,
    updateIf: (stack: List<Destination>) -> Boolean,
) = composed {
    val remoteUiStore = LocalRemoteUiStore.current
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
            remoteUiStore.updatePosition(key, coordinates.localToRoot(Offset.Zero))
        }
    } else {
        this
    }
}