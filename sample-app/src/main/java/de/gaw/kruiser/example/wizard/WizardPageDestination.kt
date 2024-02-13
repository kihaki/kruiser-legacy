package de.gaw.kruiser.example.wizard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
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
import de.gaw.kruiser.backstack.pop
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.transition.CardTransition
import de.gaw.kruiser.backstack.ui.transition.rememberIsVisible
import de.gaw.kruiser.backstack.ui.transparency.TransparentScreen
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstack
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
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
) : AndroidDestination {
    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() = TransparentScreen {
            val animDurationMs = 4_000
            val isBackgroundScrimmed by rememberIsVisible()

            val backgroundTransparency by animateFloatAsState(
                animationSpec = tween(animDurationMs),
                targetValue = if (isBackgroundScrimmed) .3f else .0f,
                label = "bottom-sheet-background-scrim",
            )
            val backstack = LocalMutableBackstack.currentOrThrow
            val backstackEntry = LocalBackstackEntry.currentOrThrow
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Black.copy(alpha = backgroundTransparency)),
//                contentAlignment = Alignment.BottomCenter,
//            ) {
            AlertDialog(
                title = {
                    title?.let { Text(text = it) }
                },
                text = {
                    message?.let { Text(text = it) }
                },
                onDismissRequest = backstack::pop,
                confirmButton = {
                    ElevatedButton(onClick = {
                        backstack.mutate {
                            filterNot { it == backstackEntry }
                                .popWizard()
                        }
                    }
                    ) {
                        Text("It's good.")
                    }
                },
            )
//            }
        }
    }
}