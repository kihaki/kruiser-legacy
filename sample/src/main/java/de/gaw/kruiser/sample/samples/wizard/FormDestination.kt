package de.gaw.kruiser.sample.samples.wizard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.AnimatedNavigation
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.android.LocalScopedServiceProvider
import de.gaw.kruiser.android.navigationStateOwnerViewModel
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.sample.samples.wizard.one.FormOneDestination
import de.gaw.kruiser.sample.samples.wizard.two.FormTwoDestination
import de.gaw.kruiser.sample.transition.HorizontalCardStackTransition
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.state.currentStack
import de.gaw.kruiser.state.pop
import de.gaw.kruiser.state.popAll
import de.gaw.kruiser.state.push
import kotlinx.coroutines.flow.collectLatest

/**
 * Marker interface for all destinations belonging to the Form example.
 */
object FormDestination : Destination {
    override fun build(): Screen = FormScreen(this)
}

private class FormScreen(override val destination: Destination) : Screen {
    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content() = HorizontalCardStackTransition {
        val parentState = LocalNavigationState.current
        val navigationViewModel = navigationStateOwnerViewModel("form").apply {
            if (state.currentStack.isEmpty()) state.push(FormOneDestination)
        }
        val navigationStack by navigationViewModel.state.collectCurrentStack()

        LaunchedEffect(navigationViewModel.state) {
            navigationViewModel.state.stack.collectLatest {
                if (it.isEmpty()) parentState.pop()
            }
        }

        CompositionLocalProvider(
            LocalScopedServiceProvider provides navigationViewModel.serviceProvider,
        ) {
            Column {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                ) {

                }
                AnimatedNavigation(
                    state = navigationViewModel.state,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val isBackVisible by remember { derivedStateOf { navigationStack.size > 1 } }
                        AnimatedVisibility(
                            visible = isBackVisible,
                        ) {
                            ElevatedButton(
                                onClick = { navigationViewModel.state.pop() }) {
                                Text("Back")
                            }
                        }
                        val label by remember {
                            derivedStateOf {
                                when (navigationStack.contains(FormTwoDestination)) {
                                    true -> "Complete"
                                    false -> "Next"
                                }
                            }
                        }
                        Crossfade(
                            modifier = Modifier.weight(1f),
                            targetState = label,
                            label = "next-button-label-anim",
                        ) {
                            ElevatedButton(
                                onClick = {
                                    when {
                                        navigationStack.contains(FormTwoDestination) -> navigationViewModel.state.popAll()
                                        else -> navigationViewModel.state.push(FormTwoDestination)
                                    }
                                }
                            ) {
                                Text(it)
                            }
                        }
                    }
                }
            }
        }
    }
}