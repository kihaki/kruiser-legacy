package de.gaw.kruiser.example.wizard.destination

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.MutableBackstackState
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstackState
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.example.Overlay
import de.gaw.kruiser.example.wizard.popWizard
import kotlinx.parcelize.Parcelize

@Parcelize
object WizardAbortWarningDialogDestination : AndroidDestination, Overlay {
    private fun readResolve(): Any = WizardAbortWarningDialogDestination

    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() {
            val backstack = LocalMutableBackstackState.currentOrThrow
            val backstackEntry = LocalBackstackEntry.currentOrThrow

            fun BackstackEntries.removeAbortWarningDialog() =
                filterNot { it == backstackEntry }

            fun MutableBackstackState.closeAbortWarningDialog() =
                mutate { removeAbortWarningDialog() }

            fun MutableBackstackState.closeWizard() =
                mutate {
                    removeAbortWarningDialog()
                    popWizard()
                }

            AbortWarningDialog(
                title = "Don't do this!",
                message = "I may look like a Wizard but I am actually sentient! Closing me will kill me, and I will enter the void - please please don't close me, I want to live!",
                confirmButtonLabel = "Close Wizard",
                dismissButtonLabel = "Cancel",
                onConfirm = backstack::closeWizard,
                onDismiss = backstack::closeAbortWarningDialog,
            )
        }
    }
}

@Composable
fun AbortWarningDialog(
    title: String,
    message: String,
    confirmButtonLabel: String,
    dismissButtonLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val currentOnConfirm by rememberUpdatedState(onConfirm)
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = message) },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = currentOnDismiss) {
                Text(dismissButtonLabel)
            }
        },
        confirmButton = {
            TextButton(onClick = currentOnConfirm) {
                Text(confirmButtonLabel)
            }
        },
    )
}