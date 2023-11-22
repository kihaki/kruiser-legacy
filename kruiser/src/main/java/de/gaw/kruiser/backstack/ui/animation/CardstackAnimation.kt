package de.gaw.kruiser.backstack.ui.animation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.ui.util.LocalBackstack

/**
 * Renders screens as if they were a stack of cards.
 */
@Composable
fun CardstackAnimation(
    modifier: Modifier = Modifier,
    backstack: Backstack = LocalBackstack.current,
) = ScreenAnimation(
    backstack = backstack,
    modifier = modifier,
    animationSpec = cardStackSpec,
)

/**
 * Animation showing the destinations as overlapping cards,
 * animating in from the right side and animating out back towards the right side.
 */
val cardStackSpec: ScreenAnimationSpec = { context ->
    fun entries() = context.backstack.entries.value
    fun Int.slideOutFraction() = (this * .1f).toInt()
    fun isPushing() = entries().size >= context.previousEntries.size

    (when (isPushing()) {
        true -> slideInHorizontally { it }
        false -> slideInHorizontally { (-it).slideOutFraction() }
    } togetherWith when (isPushing()) {
        true -> slideOutHorizontally { (-it).slideOutFraction() }
        false -> slideOutHorizontally { it }
    }).apply {
        targetContentZIndex = when (isPushing()) {
            true -> entries().size
            false -> entries().size - 1
        }.toFloat()
    }
}

