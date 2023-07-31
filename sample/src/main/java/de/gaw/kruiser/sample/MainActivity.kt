package de.gaw.kruiser.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.Navigation
import de.gaw.kruiser.android.navigationOwnerViewModel
import de.gaw.kruiser.destinationgroup.WizardDestination
import de.gaw.kruiser.sample.samples.DashboardDestination
import de.gaw.kruiser.sample.theme.KruiserTheme
import de.gaw.kruiser.screen.ScreenTransition
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.NavigationState.Event.Idle
import de.gaw.kruiser.state.NavigationState.Event.Pop
import de.gaw.kruiser.state.NavigationState.Event.Push
import de.gaw.kruiser.state.NavigationState.Event.Replace
import de.gaw.kruiser.state.currentStack
import de.gaw.kruiser.state.push
import de.gaw.kruiser.state.collectCurrentDestination
import de.gaw.kruiser.state.collectCurrentEvent
import de.gaw.kruiser.state.pop

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
                    Box {
                        val currentStack by navigationViewModel.state.stack.collectAsState()
                        val currentDestination by navigationViewModel.state.collectCurrentDestination()
                        val currentLastEvent by navigationViewModel.state.collectCurrentEvent()
                        val isWizardDestination by remember { derivedStateOf { currentDestination is WizardDestination } }
                        AnimatedVisibility(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            enter = slideInHorizontally { it },
                            exit = slideOutHorizontally { it },
                            visible = isWizardDestination,
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 24.dp,
                                        vertical = 16.dp
                                    ),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                ) {
                                    ElevatedButton(onClick = navigationViewModel.state::pop) {
                                        Text("Previous")
                                    }
                                    ElevatedButton(onClick = { }) {
                                        Text("Next")
                                    }
                                }
                            }
                        }

                        Navigation(
                            modifier = Modifier.fillMaxSize(),
                            state = navigationViewModel.state,
                            serviceProvider = navigationViewModel.serviceProvider,
                        )
                    }
                }
            }
        }
    }
}