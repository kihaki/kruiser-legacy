package de.gaw.kruiser.example.wizard

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.pop
import de.gaw.kruiser.backstack.results.LocalBackstackEntriesResultsStore
import de.gaw.kruiser.backstack.results.setResult
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
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