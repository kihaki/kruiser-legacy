package de.gaw.kruiser.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import de.gaw.kruiser.AnimatedNavigation
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.android.LocalScopedServiceProvider
import de.gaw.kruiser.android.navigationOwnerViewModel
import de.gaw.kruiser.sample.samples.DashboardDestination
import de.gaw.kruiser.sample.theme.KruiserTheme
import de.gaw.kruiser.state.currentStack
import de.gaw.kruiser.state.push

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KruiserTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navigationViewModel = navigationOwnerViewModel().apply {
                        if (state.currentStack.isEmpty()) state.push(DashboardDestination)
                    }
                    CompositionLocalProvider(
                        LocalNavigationState provides navigationViewModel.state,
                        LocalScopedServiceProvider provides navigationViewModel.serviceProvider,
                    ) {
                        AnimatedNavigation(
                            modifier = Modifier.fillMaxSize(),
                        )
//                        Box {
//                            val currentDestination by navigationViewModel.state.collectCurrentDestination()
//                            val isWizardDestination by remember { derivedStateOf { currentDestination is WizardDestination } }
//
//                            Navigation(
//                                modifier = Modifier
//                                    .fillMaxSize(),
//                                state = navigationViewModel.state,
//                                serviceProvider = navigationViewModel.serviceProvider,
//                            )
//
//                            val currentStack by navigationViewModel.state.stack.collectAsState()
//                            val currentLastEvent by navigationViewModel.state.collectCurrentEvent()
//                            val wizardControlsZIndex by remember {
//                                derivedStateOf {
//                                    when (isWizardDestination) {
//                                        true -> 1f
//                                        false -> when (currentLastEvent) {
//                                            Idle,
//                                            Push,
//                                            Replace,
//                                            -> -1f
//
//                                            Pop,
//                                            -> 1f
//                                        }
//                                    }
//                                }
//                            }
//                            AnimatedVisibility(
//                                modifier = Modifier
//                                    .align(Alignment.BottomCenter)
//                                    .zIndex(wizardControlsZIndex),
//                                enter = slideInHorizontally { it },
//                                exit = slideOutHorizontally { it },
//                                visible = isWizardDestination,
//                            ) {
//                                Surface(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(72.dp)
//                                ) {
//                                    Row(
//                                        modifier = Modifier.padding(
//                                            horizontal = 24.dp,
//                                            vertical = 16.dp
//                                        ),
//                                        horizontalArrangement = Arrangement.SpaceEvenly,
//                                    ) {
//                                        ElevatedButton(onClick = navigationViewModel.state::pop) {
//                                            Text("Previous")
//                                        }
//                                        ElevatedButton(onClick = { }) {
//                                            Text("Next")
//                                        }
//                                    }
//                                }
//                            }
//                        }
                    }
                }
            }
        }
    }
}