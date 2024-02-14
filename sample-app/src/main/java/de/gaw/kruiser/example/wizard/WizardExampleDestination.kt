package de.gaw.kruiser.example.wizard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.MutableBackstack
import de.gaw.kruiser.backstack.debug.DebugBackstackLoggerEffect
import de.gaw.kruiser.backstack.pop
import de.gaw.kruiser.backstack.push
import de.gaw.kruiser.backstack.ui.Backstack
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.transition.BottomCardTransition
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstack
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
object WizardExampleDestination : AndroidDestination {
    private fun readResolve(): Any = WizardExampleDestination

    override fun build(): Screen = object : Screen {

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        override fun Content() = BottomCardTransition {
            val parentBackstack = LocalMutableBackstack.currentOrThrow
            val wizardBackstack = rememberSaveableBackstack(WizardPageDestination(1))
            val backstackEntry = LocalBackstackEntry.currentOrThrow
            val pageCount = 5
            val wizardEntries by wizardBackstack.collectEntries()
            Scaffold(
                topBar = {
                    Surface(shadowElevation = 2.dp) {
                        TopAppBar(
                            title = {
                                Column {
                                    Text("Wizard")
                                    val progress by animateFloatAsState(
                                        targetValue = (wizardEntries.size - 1).toFloat() / (pageCount - 1),
                                        label = "wizard-progress",
                                    )
                                    LinearProgressIndicator(progress = { progress })
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    parentBackstack.push(
                                        WarningDialogDestination(
                                            title = "Warning",
                                            message = "Nah, all good actually."
                                        )
                                    )
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Close Wizard"
                                    )
                                }
                            },
                        )
                    }
                },
                content = {
                    Backstack(
                        modifier = Modifier.padding(it),
                        backstack = wizardBackstack,
                    )
                    DebugBackstackLoggerEffect(
                        tag = "Wizard ${backstackEntry.id} backstack",
                        backstack = wizardBackstack,
                    )
                },
                bottomBar = {
                    Surface(
                        modifier = Modifier.imePadding(),
                        shadowElevation = 2.dp,
                    ) {
                        BottomAppBar {
                            val backEnabled = wizardEntries.size > 1
                            ListItem(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(
                                        enabled = backEnabled,
                                        onClick = wizardBackstack::pop,
                                    ),
                                headlineContent = {
                                    val textAlpha by animateFloatAsState(
                                        targetValue = if (backEnabled) 1f else .5f,
                                        label = "back-alpha",
                                    )
                                    Text(modifier = Modifier.alpha(textAlpha), text = "Back")
                                },
                            )
                            ListItem(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(
                                        onClick = {
                                            when {
                                                wizardEntries.size < pageCount -> wizardBackstack.push(
                                                    WizardPageDestination(wizardEntries.size + 1)
                                                )

                                                else -> parentBackstack.popWizard()
                                            }
                                        },
                                    ),
                                headlineContent = {
                                    AnimatedContent(
                                        wizardEntries.size < pageCount,
                                        transitionSpec = {
                                            slideInVertically { it } togetherWith
                                                    slideOutVertically { -it }
                                        },
                                        label = "next-button-label-transition",
                                    ) { isFinish ->
                                        Row {
                                            when (isFinish) {
                                                true -> {
                                                    Text("Next")
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Icon(
                                                        Icons.AutoMirrored.Filled.ArrowForward,
                                                        "next-icon"
                                                    )
                                                }

                                                else -> {
                                                    Text("Finish")
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Icon(Icons.Filled.Done, "done-icon")
                                                }
                                            }
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            )
        }
    }
}

fun MutableBackstack.popWizard() = mutate {
    popWizard()
}

fun BackstackEntries.popWizard() =
    findLast { it.destination is WizardExampleDestination }?.let { wizard ->
        this - wizard
    } ?: this