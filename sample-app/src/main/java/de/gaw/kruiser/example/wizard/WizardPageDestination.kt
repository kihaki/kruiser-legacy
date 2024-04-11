package de.gaw.kruiser.example.wizard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
object WizardNameDestination :
    AndroidDestination,
    WizardDestination,
    ModalTransition {
    private fun readResolve(): Any = WizardNameDestination

    override val wizardState: WizardState
        get() = DefaultWizardState(
            title = "Your Name",
            progress = (wizardDestinations.indexOf(this@WizardNameDestination) + 1) / wizardDestinations.size.toFloat(),
        )

    override fun build() = object : Screen {
        @Composable
        override fun Content() {
            WizardEntryPageContent()
        }
    }
}

@Parcelize
object WizardNicknameDestination :
    AndroidDestination,
    WizardDestination,
    ModalTransition {
    private fun readResolve(): Any = WizardNicknameDestination

    override val wizardState: WizardState
        get() = DefaultWizardState(
            title = "Your Nickname",
            progress = (wizardDestinations.indexOf(this@WizardNicknameDestination) + 1) / wizardDestinations.size.toFloat(),
        )

    override fun build() = object : Screen {

        @Composable
        override fun Content() {
            WizardEntryPageContent()
        }
    }
}

@Composable
private fun WizardEntryPageContent() {
    Surface(
        shadowElevation = 16.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val destination = LocalBackstackEntry.currentOrThrow
                    .destination as WizardDestination
                Text(
                    text = destination.wizardState.title,
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

@Parcelize
object WizardCompletionDestination :
    AndroidDestination,
    WizardDestination,
    ModalTransition {
    private fun readResolve(): Any = WizardCompletionDestination

    override val wizardState: WizardState
        get() = DefaultWizardState(
            title = "Completed?",
            progress = (wizardDestinations.indexOf(this@WizardCompletionDestination) + 1) / wizardDestinations.size.toFloat(),
        )

    override fun build() = object : Screen {

        @Composable
        override fun Content() {
            Surface(
                shadowElevation = 4.dp,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Is this correct?",
                            style = MaterialTheme.typography.displayLarge,
                        )
                    }
                }
            }
        }
    }
}
