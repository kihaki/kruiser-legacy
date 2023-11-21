package de.gaw.kruiser.backstack.ui.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.Entries
import de.gaw.kruiser.backstack.ui.ScreenContent
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.util.rememberPreviousBackstackOf
import de.gaw.kruiser.destination.Destination

interface ScreenAnimationContext {
    val backstack: Backstack
    val previousEntries: Entries
}

private data class ScreenAnimationContextImpl(
    override val backstack: Backstack,
    override val previousEntries: Entries,
) : ScreenAnimationContext

typealias ScreenAnimationSpec = AnimatedContentTransitionScope<Destination?>.(ScreenAnimationContext) -> ContentTransform

@Composable
fun ScreenAnimation(
    backstack: Backstack,
    modifier: Modifier = Modifier,
    animationSpec: ScreenAnimationSpec,
) {
    val entries by backstack.collectEntries()

    val previousBackstack = rememberPreviousBackstackOf(backstack)
    val previousEntries by previousBackstack.collectEntries()

    AnimatedContent(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        targetState = entries.lastOrNull(),
        transitionSpec = {
            animationSpec(
                this,
                ScreenAnimationContextImpl(
                    backstack = backstack,
                    previousEntries = previousEntries
                )
            )
        },
        label = "cardstack-animation"
    ) { destination ->
        when (destination) {
            null -> Spacer(modifier = modifier.fillMaxSize())
            else -> ScreenContent(destination)
        }
    }
}

/**
 * Animation showing the destinations as overlapping cards,
 * animating in from the right side and animating out back towards the right side.
 */
val cardStackAnimationSpec: ScreenAnimationSpec = { context ->
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