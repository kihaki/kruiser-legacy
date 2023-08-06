package de.gaw.kruiser

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.collectIsEmpty
import de.gaw.kruiser.state.pop
import de.gaw.kruiser.transition.LocalAnimatedNavigationState
import de.gaw.kruiser.transition.collectStack
import de.gaw.kruiser.transition.rememberAnimatedNavigationState

@Composable
fun AnimatedNavigation(
    modifier: Modifier = Modifier,
    state: NavigationState = LocalNavigationState.current,
) {
    val isEmpty by state.collectIsEmpty()

    BackHandler(
        enabled = !isEmpty,
        onBack = state::pop,
    )

    val animatedNavigationState = rememberAnimatedNavigationState(navigationState = state)
    val navigationStack by animatedNavigationState.collectStack()

    Box(modifier = modifier) {
        CompositionLocalProvider(
            LocalAnimatedNavigationState provides animatedNavigationState
        ) {
            navigationStack.forEachIndexed { index, (destination, _) ->
                key(destination) {
                    SideEffect {
                        Log.v("AnimationThing", "$index Rendering $destination")
                    }
                    val screen = remember(destination) { destination.build() }
                    screen.Content()
                }
            }
        }
        SideEffect {
            Log.v("AnimationThing", "===")
        }
//        CompositionLocalProvider(
//            LocalExitAnimationsState provides renderState,
//        ) {
//            val destinationsToRender by renderState.collectOnScreenDestinations()
//            destinationsToRender
//                .filterInvisible()
//                .forEachIndexed { index, destination ->
//                    key(destination) {
//                        SideEffect {
//                            Log.v("AnimationThing", "$index Rendering $destination")
//                        }
//                        val screen = remember(destination) { destination.build() }
//                        screen.Content()
//                    }
//                }
//            SideEffect {
//                Log.v("AnimationThing", "===")
//            }
//        }
    }
}

fun List<Destination>.filterInvisible(): List<Destination> = takeLast(2)