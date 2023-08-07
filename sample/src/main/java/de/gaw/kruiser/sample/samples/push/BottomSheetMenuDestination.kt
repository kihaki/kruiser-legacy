package de.gaw.kruiser.sample.samples.push

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.android.LocalScopedServiceProvider
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.service.ClearDeadServicesDisposableEffect
import de.gaw.kruiser.service.ScopedServiceProvider
import de.gaw.kruiser.transition.AnimatedNavigationState
import de.gaw.kruiser.transition.LocalAnimatedNavigationState
import de.gaw.kruiser.transition.collectTransitionState

data class BottomSheetMenuDestination(
    val title: String,
) : Destination {
    override fun build(): Screen = object : Screen {
        override val destination: Destination = this@BottomSheetMenuDestination

        @Composable
        override fun Content() = BottomSheetTransition {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.headlineMedium
                    ) {
                        Text(text = title)
                    }
                }
            }
        }
    }
}

@Composable
fun Screen.BottomSheetTransition(
    inSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    outSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    navigationState: AnimatedNavigationState = LocalAnimatedNavigationState.current,
    scopedServiceProvider: ScopedServiceProvider = LocalScopedServiceProvider.current,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val screenTransitionState by navigationState.collectTransitionState(destination = destination)

    val backgroundEnterTransition = remember(navigationState) {
        fadeIn(tween(350))
    }
    val backgroundExitTransition = remember(navigationState) {
        fadeOut(tween(350))
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visibleState = screenTransitionState,
            enter = backgroundEnterTransition,
            exit = backgroundExitTransition,
            content = {
                Box(
                    modifier = Modifier
                        .clickable(
                            onClick = {},
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        )
                        .background(color = Color.Black.copy(alpha = .7f))
                        .fillMaxSize()
                )
            },
        )

        val menuEnterTransition = remember(navigationState) {
            slideInVertically(inSpec) { size: Int -> size }
        }
        val menuExitTransition = remember(navigationState) {
            slideOutVertically(outSpec) { size: Int -> size }
        }

        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .heightIn(min = 192.dp),
            visibleState = screenTransitionState,
            enter = menuEnterTransition,
            exit = menuExitTransition,
            content = {
                ClearDeadServicesDisposableEffect(scopedServiceProvider = scopedServiceProvider)
                content()
            },
        )
    }
}
