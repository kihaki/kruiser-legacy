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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.backstack.results.LocalBackstackEntriesResultsStore
import de.gaw.kruiser.backstack.results.rememberResult
import de.gaw.kruiser.backstack.results.setOrMutateResult
import de.gaw.kruiser.backstack.ui.transition.CardTransition
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.example.wizard.WizardExampleDestination.WizardResult
import kotlinx.parcelize.Parcelize

interface WizardPageDestination : AndroidDestination {
    fun WizardResult.updateResult(input: String): WizardResult
    val label: String

    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() = CardTransition {
            Surface(
                shadowElevation = 4.dp,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.displayLarge,
                        )
                        var textContent by rememberSaveable {
                            mutableStateOf("")
                        }

                        val results = LocalBackstackEntriesResultsStore.currentOrThrow
                        LaunchedEffect(textContent) {
                            if (textContent.isNotBlank()) {
                                results.setOrMutateResult(
                                    WizardResult().updateResult(textContent)
                                ) {
                                    updateResult(textContent)
                                }
                            }
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
object WizardNameDestination : WizardPageDestination {
    private fun readResolve(): Any = WizardNameDestination
    override fun WizardResult.updateResult(input: String): WizardResult = copy(name = input)
    override val label: String get() = "Your Name"
}

@Parcelize
object WizardNicknameDestination : WizardPageDestination {
    private fun readResolve(): Any = WizardNameDestination
    override fun WizardResult.updateResult(input: String): WizardResult = copy(nickname = input)
    override val label: String get() = "Your Nickname"
}

@Parcelize
object WizardCompletionDestination : AndroidDestination {
    private fun readResolve(): Any = WizardCompletionDestination

    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() = CardTransition {
            Surface(
                shadowElevation = 4.dp,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
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
                        val input: WizardResult? by rememberResult()
                        Text(
                            text = "Name: ${input?.name?.takeUnless { it.isBlank() } ?: "(Blank)"}",
                        )
                        Text(
                            text = "Nickname: ${input?.nickname?.takeUnless { it.isBlank() } ?: "(Blank)"}",
                        )
                    }
                }
            }
        }
    }
}
