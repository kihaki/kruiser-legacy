package de.gaw.kruiser.renderer

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.IntOffset
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.util.filterDestinations
import de.gaw.kruiser.example.Overlay
import de.gaw.kruiser.example.transition.ModalTransition
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

@Composable
fun <S> BackstackState.slideTransition(): AnimatedContentTransitionScope<S>.() -> ContentTransform {
    val animData by produceState(false to false) {
        var previousEntries = emptyList<BackstackEntry>()
        entries.collectLatest {
            val withoutOverlays = it.filterDestinations { it !is Overlay }
            val previousWithoutOverlays = previousEntries.filterDestinations { it !is Overlay }
            val isWizard =
                ((previousWithoutOverlays.lastOrNull()?.destination is ModalTransition) xor (withoutOverlays.lastOrNull()?.destination is ModalTransition))
            value = (it.size >= previousEntries.size) to isWizard
            previousEntries = it
        }
    }
    val (hasPushed, isWizard) = animData
    val inTween = spring(
        stiffness = Spring.StiffnessMediumLow,
        visibilityThreshold = IntOffset.VisibilityThreshold
    )
    val outTween = spring(
        stiffness = Spring.StiffnessMediumLow,
        visibilityThreshold = IntOffset.VisibilityThreshold
    )
    val entries by collectEntries()
    val stackSize = entries.size
    return {
        if (hasPushed) {
            if (isWizard) {
                slideInVertically(inTween) { it } togetherWith
                        slideOutVertically(outTween) { (it * .05f).roundToInt() } +
                        scaleOut(targetScale = .94f)
            } else {
                slideInHorizontally(inTween) { it } togetherWith
                        slideOutHorizontally(outTween) { -it / 2 }
            }
        } else {
            if (isWizard) {
                (slideInVertically(inTween) { (it * .05f).roundToInt() } + scaleIn(initialScale = .94f)) togetherWith
                        slideOutVertically(outTween) { it }
            } else {
                slideInHorizontally(inTween) { -it / 2 } togetherWith
                        slideOutHorizontally(outTween) { it }
            }
        }.apply {
            targetContentZIndex = when {
                // Make sure that content that's pushed is rendered on top
                hasPushed -> stackSize
                // Make sure that content that's popped is rendered on top
                else -> (stackSize - 1)
            }.toFloat()
        }
    }
}