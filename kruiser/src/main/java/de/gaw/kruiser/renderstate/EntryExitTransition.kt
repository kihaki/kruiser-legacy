package de.gaw.kruiser.renderstate

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.android.LocalScopedServiceProvider
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.collectCurrentStack

@Composable
fun Screen.EntryExitTransition(
    inAnimation: NavigationState.() -> EnterTransition,
    outAnimation: NavigationState.() -> ExitTransition,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val navState = LocalNavigationState.current
    val serviceProvider = LocalScopedServiceProvider.current
    val renderState = LocalDestinationRenderState.current

    val stack by navState.collectCurrentStack()

    val visibleDestinations by renderState.collectVisibleDestinations()
    val zIndex by remember { derivedStateOf { visibleDestinations[destination]?.zIndex ?: 0f } }
    val isOnStack by remember { derivedStateOf { stack.contains(destination) } } // TODO: Tweak

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(isOnStack) {
        Log.v(
            "AnimationThing",
            "$destination isVisible: $isOnStack at ${System.currentTimeMillis()} at ${zIndex.toInt()}"
        )
        isVisible = isOnStack  // TODO: Tweak
    }

    val enterTransition = remember(navState) { inAnimation(navState) }
    val exitTransition = remember(navState) { outAnimation(navState) }

    AnimatedVisibility(
        modifier = Modifier
            .zIndex(zIndex),
        visible = isVisible,
        enter = enterTransition,
        exit = exitTransition,
        content = {
            DisposableEffect(Unit) {
                onDispose {
                    Log.v(
                        "AnimationThing",
                        "Disposing $destination at ${System.currentTimeMillis()} at ${zIndex.toInt()}"
                    )
                    // When this composable leaves the composition (= out-animation is done),
                    // check if any services need to die
                    serviceProvider.clearDeadServices()
                    renderState.onAnimatedOut(destination)
                }
            }
            content()
        },
    )
}