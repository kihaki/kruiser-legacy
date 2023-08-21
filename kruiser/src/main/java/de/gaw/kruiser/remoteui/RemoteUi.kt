package de.gaw.kruiser.remoteui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.zIndex
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.toIntOffset
import de.gaw.kruiser.transition.ExitTransitionTracker
import de.gaw.kruiser.transition.LocalExitTransitionTracker
import de.gaw.kruiser.transition.collectCurrentExitTransition

@Composable
inline fun <reified T: Destination> RemoteUi(
    key: RemoteUiKey,
    noinline content: @Composable () -> Unit,
) = RemoteUi(
    key = key,
    zIndexCalculation = attachToTopDestinationType<T>(),
    content = content,
)

/**
 * Will attach the zIndex to the zIndex of the top most [Destination] that is of type [T].
 * This works well as a no-brainer default value.
 */
inline fun <reified T: Destination> attachToTopDestinationType() : RemoteUiContext.() -> Float = {
    stack.indexOfLast { it is T }.toFloat()
}

/**
 * Will render the remote ui.
 * This will render at the same position as the matching [RemoteUiLayout].
 * The matching [RemoteUiLayout] will render somewhere else at the same
 * size as this placeholder.
 */
@Composable
fun RemoteUi(
    key: RemoteUiKey,
    zIndexCalculation: RemoteUiContext.() -> Float,
    content: @Composable () -> Unit,
) {
    val remoteUiCoordinator: RemoteUiCoordinator = LocalRemoteUiCoordinator.current
    val navigationState: NavigationState = LocalNavigationState.current

    val remoteUiContext by rememberRemoteUiContext(navigationState = navigationState)

    // Calculate zIndex of the RemoteUi so that it's correctly layered into the Navigation Stack
    val currentZIndexCalculation by rememberUpdatedState(zIndexCalculation)
    val zIndex by remember {
        derivedStateOf { remoteUiContext.currentZIndexCalculation() }
    }

    // Get the position and visibility of the placeholder layout 
    // so we know where to position the actual layout
    val placeHolder by remoteUiCoordinator.collectLayout(key)
    if (placeHolder.isVisible) {
        Box(
            modifier = Modifier
                .applyRemoteUiOffset(key) // apply the placeholders position
                .updateRemoteUiSize(key) // update the placeholders size
                .zIndex(zIndex),
        ) {
            content()
        }
    }
}

/**
 * Context for RemoteUi, for calculating the zIndex for example.
 */
data class RemoteUiContext(
    val navigationState: NavigationState,
    val stack: List<Destination>,
)

@Composable
private fun rememberRemoteUiContext(
    navigationState: NavigationState = LocalNavigationState.current,
    exitTransitionTracker: ExitTransitionTracker = LocalExitTransitionTracker.current,
) : State<RemoteUiContext> {
    val stack by navigationState.collectCurrentStack()
    val exitTransition by exitTransitionTracker.collectCurrentExitTransition()
    val animatedStack by remember {
        derivedStateOf { (stack + exitTransition?.destination).filterNotNull() }
    }

    return remember(navigationState) {
        derivedStateOf {
            RemoteUiContext(
                navigationState = navigationState,
                stack = animatedStack,
            )
        }
    }
}

/**
 * Applies the offset of the placeholder to this composable.
 * Used to position the actual RemoteUi at the position of the current placeholder.
 */
private fun Modifier.applyRemoteUiOffset(key: RemoteUiKey) = composed {
    val remoteUiCoordinator = LocalRemoteUiCoordinator.current
    val layout by remoteUiCoordinator.collectLayout(key)
    val remoteUiPosition by remember { derivedStateOf { layout.position.toIntOffset() } }
    absoluteOffset { remoteUiPosition }
}

/**
 * Updates the RemoteUiLayout to be synced with the size of this composable.
 * Used to size the placeholders according to the size of the RemoteUi.
 */
private fun Modifier.updateRemoteUiSize(key: RemoteUiKey) = composed {
    val remoteUiCoordinator = LocalRemoteUiCoordinator.current
    val density = LocalDensity.current
    onSizeChanged { size ->
        val dpSize = with(density) {
            with(size) {
                DpSize(width.toDp(), height.toDp())
            }
        }
        remoteUiCoordinator.updateSize(key, dpSize)
    }
}