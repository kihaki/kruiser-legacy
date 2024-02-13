package de.gaw.kruiser.example

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.backstack.pop
import de.gaw.kruiser.backstack.push
import de.gaw.kruiser.backstack.ui.Backstack
import de.gaw.kruiser.backstack.ui.transition.CardTransition
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstack
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
object ExampleListDestination : AndroidDestination {
    private fun readResolve(): Any = ExampleListDestination

    override fun build(): Screen = object : Screen {

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        override fun Content() = CardTransition {
            Scaffold(
                topBar = { TopAppBar(title = { Text("Kruiser Samples") }) }
            ) {
                val backstack = LocalMutableBackstack.currentOrThrow
                LazyColumn(contentPadding = it) {
                    item {
                        ListItem(
                            modifier = Modifier
                                .clickable { backstack.push(WizardExampleDestination) },
                            headlineContent = { Text("Wizard") },
                        )
                    }
                }
            }
        }
    }
}

@Parcelize
data class WizardPageDestination(val page: Int) : AndroidDestination {
    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() = CardTransition {
            Surface(
                shadowElevation = 4.dp,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$page",
                        style = MaterialTheme.typography.displayLarge,
                    )
                }
            }
        }
    }
}

@Parcelize
object WizardExampleDestination : AndroidDestination {
    private fun readResolve(): Any = WizardExampleDestination

    override fun build(): Screen = object : Screen {

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        override fun Content() = CardTransition {
            val parentBackstack = LocalMutableBackstack.currentOrThrow
            val wizardBackstack = rememberSaveableBackstack(WizardPageDestination(1))
            val pageCount = 5
            val wizardEntries by wizardBackstack.collectEntries()
            Scaffold(
                topBar = {
                    TopAppBar(title = {
                        Column {
                            Text("Wizard")
                            LinearProgressIndicator(progress = { ((wizardEntries.size - 1).toFloat() / (pageCount - 1)) })
                        }
                    })
                },
                content = {
                    Backstack(
                        modifier = Modifier.padding(it),
                        backstack = wizardBackstack,
                    )
                },
                bottomBar = {
                    BottomAppBar {
                        if (wizardEntries.size > 1) {
                            ListItem(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(onClick = wizardBackstack::pop),
                                headlineContent = { Text("Back") },
                            )
                        }
                        ListItem(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    onClick = {
                                        when {
                                            wizardEntries.size < pageCount -> wizardBackstack.push(
                                                WizardPageDestination(wizardEntries.size + 1)
                                            )

                                            else -> parentBackstack.mutate {
                                                findLast { it.destination is WizardExampleDestination }?.let { wizard ->
                                                    this - wizard
                                                } ?: this
                                            }
                                        }
                                    },
                                ),
                            headlineContent = {
                                AnimatedContent(
                                    when {
                                        wizardEntries.size < pageCount -> "Next"
                                        else -> "Finish"
                                    },
                                    label = "next-button-label-transition",
                                ) {
                                    Text(text = it)
                                }
                            },
                        )
                    }
                }
            )
        }
    }
}