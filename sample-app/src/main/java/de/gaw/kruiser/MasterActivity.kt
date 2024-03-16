package de.gaw.kruiser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import de.gaw.kruiser.PageStyle.Regular
import de.gaw.kruiser.PageStyle.Wizard
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.debug.DebugBackstackLoggerEffect
import de.gaw.kruiser.backstack.savedstate.PersistedMutableBackstack
import de.gaw.kruiser.backstack.ui.BackstackContext
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.rendering.Render
import de.gaw.kruiser.backstack.ui.transparency.Overlay
import de.gaw.kruiser.backstack.ui.util.collectDerivedEntries
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.util.filterDestinations
import de.gaw.kruiser.backstack.util.rememberScreen
import de.gaw.kruiser.example.ExamplesListDestination
import de.gaw.kruiser.example.wizard.DefaultWizardState
import de.gaw.kruiser.example.wizard.ModalTransition
import de.gaw.kruiser.example.wizard.Wizard
import de.gaw.kruiser.example.wizard.WizardDestination
import de.gaw.kruiser.example.wizard.WizardScreen
import de.gaw.kruiser.ui.theme.KruiserSampleTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

class MasterNavigationViewModel(savedState: SavedStateHandle) : ViewModel() {
    val backstack = savedState.PersistedMutableBackstack(
        id = "nav:master",
        initial = listOf(
            ExamplesListDestination,
        ),
    )
}

@Composable
fun activityViewModelStoreOwner() =
    LocalContext.current as ViewModelStoreOwner

@Composable
fun masterNavigationStateViewModel(): MasterNavigationViewModel =
    viewModel(
        viewModelStoreOwner = activityViewModelStoreOwner(),
        factory = viewModelFactory {
            addInitializer(MasterNavigationViewModel::class) {
                MasterNavigationViewModel(createSavedStateHandle())
            }
        },
    )

sealed class PageStyle {
    data class Regular(
        val backstackEntry: BackstackEntry?,
    ) : PageStyle()

    data object Wizard : PageStyle()
}

class MasterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            KruiserSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val masterBackstack = masterNavigationStateViewModel().backstack
                    BackstackContext(
                        backstackState = masterBackstack,
                    ) { backstack ->
                        val regular by backstack.collectDerivedEntries {
                            filterDestinations { it !is Overlay && it !is WizardDestination }
                        }
                        val overlay by backstack.collectDerivedEntries {
                            listOfNotNull(lastOrNull()?.takeIf { it.destination is Overlay })
                        }
                        val onWizard by backstack.collectDerivedEntries {
                            filterDestinations { it is WizardDestination }
                        }
                        val style by remember {
                            derivedStateOf {
                                when {
                                    onWizard.isNotEmpty() -> Wizard
                                    else -> Regular(regular.lastOrNull())
                                }
                            }
                        }
                        Box {
                            AnimatedContent(
                                targetState = style,
                                label = "main-screen-animations",
                                transitionSpec = backstack.slideTransition(),
                            ) { currentStyle ->
                                when (currentStyle) {
                                    Wizard -> {
                                        val wizardEntry by produceState(onWizard.lastOrNull()) {
                                            snapshotFlow { onWizard }
                                                .map { it.lastOrNull() }
                                                .filterNotNull()
                                                .collectLatest {
                                                    value = it
                                                }
                                        }
                                        CompositionLocalProvider(LocalBackstackEntry provides wizardEntry) {
                                            val screen = (wizardEntry?.rememberScreen() as? WizardScreen)
                                            Wizard(
                                                wizardState = screen?.wizardState ?: remember {
                                                    DefaultWizardState(
                                                        title = "Wizard",
                                                        progress = 0f,
                                                    )
                                                }
                                            ) {
                                                Box(modifier = Modifier.padding(it)) {
                                                    AnimatedContent(
                                                        targetState = wizardEntry,
                                                        label = "wizard-animator",
                                                        transitionSpec = backstack.slideTransition(),
                                                    ) { currentEntry ->
                                                        screen?.let { currentEntry?.Render(screen = it) } ?: currentEntry?.Render()
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    else -> {
                                        val entry =
                                            (currentStyle as Regular).backstackEntry
                                        entry?.Render()
                                    }
                                }
                            }
                            AnimatedContent(
                                targetState = overlay.lastOrNull(),
                                label = "overlay-transitions",
                            ) { entry ->
                                entry?.Render()
                            }
                        }
                    }

                    DebugBackstackLoggerEffect(
                        tag = "MasterBackstack",
                        backstack = masterBackstack,
                    )
                }
            }
        }
    }
}

@Composable
fun <S> BackstackState.slideTransition(): AnimatedContentTransitionScope<S>.() -> ContentTransform {
    val animData by produceState(false to false) {
        var previousEntries = emptyList<BackstackEntry>()
        entries.collectLatest {
            val withoutOverlays = it.filterDestinations { it !is Overlay }
            val previousWithoutOverlays = previousEntries.filterDestinations { it !is Overlay }
            val isWizard = ((previousWithoutOverlays.lastOrNull()?.destination is ModalTransition) xor (withoutOverlays.lastOrNull()?.destination is ModalTransition))
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
            if(isWizard) {
                slideInVertically(inTween) { it } togetherWith
                        slideOutVertically(outTween) { (it * .05f).roundToInt() } +
                        scaleOut(targetScale = .94f)
            } else {
                slideInHorizontally(inTween) { it } togetherWith
                        slideOutHorizontally(outTween) { -it / 2 }
            }
        } else {
            if(isWizard) {
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
