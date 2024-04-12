package de.gaw.kruiser.renderer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.rendering.Render
import de.gaw.kruiser.backstack.ui.util.collectDerivedEntries
import de.gaw.kruiser.backstack.util.filterDestinations
import de.gaw.kruiser.example.Overlay
import de.gaw.kruiser.example.wizard.WizardDestination
import de.gaw.kruiser.example.wizard.ui.Wizard
import de.gaw.kruiser.renderer.RenderStyle.Regular
import de.gaw.kruiser.renderer.RenderStyle.Wizard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

sealed class RenderStyle {
    data object Regular : RenderStyle()
    data object Wizard : RenderStyle()
}

@Composable
fun BackstackState.collectMostRecentOverlayBackstackEntry() = collectDerivedEntries {
    listOfNotNull(lastOrNull()?.takeIf { it.destination is Overlay })
}

@Composable
fun BackstackState.rememberWizardBackstackEntries() = collectDerivedEntries {
    filterOverlays().takeLastWhile { it.destination is WizardDestination }
}

@Composable
fun BackstackState.rememberRegularBackstackEntries() = collectDerivedEntries {
    filterOverlays().filterWizardDestinations()
}

private fun List<BackstackEntry>.filterOverlays() = filterDestinations { it !is Overlay }
private fun List<BackstackEntry>.filterWizardDestinations() = filterDestinations { it !is WizardDestination }

/**
 * This function is used to render the destinations of the provided backstack,
 * supporting both regular and wizard destinations.
 */
@Composable
fun RenderDestinations(backstack: BackstackState) {
    val regularEntries = backstack.rememberRegularBackstackEntries()
    val wizardEntries = backstack.rememberWizardBackstackEntries()

    val currentWizardEntries by wizardEntries

    val style = when {
        currentWizardEntries.isNotEmpty() -> Wizard
        else -> Regular
    }

    AnimatedContent(
        targetState = style,
        label = "main-screen-animations",
        transitionSpec = backstack.slideTransition(),
    ) { currentStyle ->
        val currentRegularEntries by regularEntries

        when (currentStyle) {
            Wizard -> RenderWizard(
                transitionSpec = backstack.slideTransition(),
                wizardEntries = wizardEntries,
            )

            Regular -> {
                currentRegularEntries.lastOrNull()?.Render()
            }
        }
    }
}

@Composable
fun RenderOverlays(backstack: BackstackState) {
    val overlays by backstack.collectMostRecentOverlayBackstackEntry()
    AnimatedContent(
        targetState = overlays.lastOrNull(),
        label = "overlay-transitions",
    ) { entry ->
        entry?.Render()
    }
}

@Composable
fun RenderWizard(
    transitionSpec: AnimatedContentTransitionScope<BackstackEntry?>.() -> ContentTransform,
    wizardEntries: State<List<BackstackEntry>>,
) {
    val currentWizardEntries by wizardEntries
    val wizardEntry by produceState(currentWizardEntries.lastOrNull()) {
        snapshotFlow { currentWizardEntries }
            .map { it.lastOrNull() }
            .filterNotNull()
            .collectLatest {
                value = it
            }
    }
    CompositionLocalProvider(LocalBackstackEntry provides wizardEntry) {
        Wizard { padding ->
            Box(modifier = Modifier.padding(padding)) {
                AnimatedContent(
                    targetState = wizardEntry,
                    label = "wizard-animator",
                    transitionSpec = transitionSpec,
                ) { currentEntry ->
                    currentEntry?.Render()
                }
            }
        }
    }
}