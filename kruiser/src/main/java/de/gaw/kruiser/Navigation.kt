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
import androidx.compose.ui.zIndex
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.persistent.PersistentUi
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
            navigationStack
                .forEachIndexed { index, (destination, transition) ->
                    key(destination) {
                        SideEffect {
                            Log.v(
                                "RemoveAnimationRender",
                                "$index Rendering $destination -> isVisible: ${transition.targetState}"
                            )
                        }
                        val screen = remember(destination) { destination.build() }
                        screen.Content()
                    }
                }
            PersistentUi()
        }
        SideEffect {
            Log.v("RemoveAnimationRender", "===")
        }
    }
}

// TODO: Do something less naive for these checks
private fun <T> List<T>.filterInvisible(): List<T> = takeLast(2)