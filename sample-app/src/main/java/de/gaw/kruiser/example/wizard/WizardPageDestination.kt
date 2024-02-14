package de.gaw.kruiser.example.wizard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.pop
import de.gaw.kruiser.backstack.results.LocalBackstackEntriesResultsStore
import de.gaw.kruiser.backstack.results.setResult
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.transition.CardTransition
import de.gaw.kruiser.backstack.ui.transparency.Transparent
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstack
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "$page",
                            style = MaterialTheme.typography.displayLarge,
                        )
                        var textContent by rememberSaveable {
                            mutableStateOf("")
                        }
                        OutlinedTextField(
                            value = textContent,
                            onValueChange = { textContent = it },
                        )
                    }
                }
            }
        }
    }
}

@Parcelize
data class WarningDialogDestination(
    val title: String? = null,
    val message: String? = null,
) : AndroidDestination,
    Transparent {
    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() {
            val backstack = LocalMutableBackstack.currentOrThrow
            val backstackEntry = LocalBackstackEntry.currentOrThrow
            fun BackstackEntries.removeDialog() = filterNot { it == backstackEntry }
            val results = LocalBackstackEntriesResultsStore.currentOrThrow

            AlertDialog(
                title = {
                    title?.let { Text(text = it) }
                },
                text = {
                    message?.let { Text(text = it) }
                },
                onDismissRequest = backstack::pop,
                dismissButton = {
                    TextButton(
                        onClick = {
                            backstack.mutate {
                                removeDialog()
                            }
                        }
                    ) {
                        Text("Actually no")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            MainScope().launch(Dispatchers.IO) {
                                backstack.mutate {
                                    removeDialog()
                                }
                                delay(90)
                                backstack.mutate {
                                    popWizard()
                                }
                                results.setResult(WizardExampleDestination.Result("This is some test text hihi"))
                            }
                        }
                    ) {
                        Text("Cool beans")
                    }
                },
            )
        }
    }
}