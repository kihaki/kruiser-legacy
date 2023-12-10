package de.gaw.kruiser.backstack.ui.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.BackstackEntries
import de.gaw.kruiser.backstack.ui.ScreenContent
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.util.rememberPreviousBackstackOf
import de.gaw.kruiser.destination.Destination

interface ScreenAnimationContext {
    val backstack: Backstack
    val previousEntries: BackstackEntries
}

private data class ScreenAnimationContextImpl(
    override val backstack: Backstack,
    override val previousEntries: BackstackEntries,
) : ScreenAnimationContext

typealias ScreenAnimationSpec = AnimatedContentTransitionScope<Destination?>.(ScreenAnimationContext) -> ContentTransform

@Composable
fun ScreenAnimation(
    backstack: Backstack,
    modifier: Modifier = Modifier,
    label: String = "screen-animation",
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
        label = label,
    ) { destination ->
        when (destination) {
            null -> Spacer(modifier = modifier.fillMaxSize())
            else -> ScreenContent(destination)
        }
    }
}