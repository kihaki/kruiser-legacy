package de.gaw.kruiser.example

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.AutoMirrored.Sharp
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.ui.BackstackContext
import de.gaw.kruiser.backstack.ui.rendering.Render
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstackState
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Preview
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.ui.theme.KruiserSampleTheme
import kotlinx.parcelize.Parcelize

@Parcelize
object NestedBackstackExampleDestination : AndroidDestination {
    private fun readResolve(): Any = NestedBackstackExampleDestination

    @OptIn(ExperimentalMaterial3Api::class)
    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() {
            val parentBackstackState = LocalMutableBackstackState.currentOrThrow
            val parentEntries by parentBackstackState.collectEntries()

            val nestedBackstackState = rememberSaveableBackstack(
                backstackId = "nested-in-scaffold",
                initial = SimplePageDestination(level = 1),
            )
            val nestedEntries by nestedBackstackState.collectEntries()
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            AnimatedVisibility(visible = parentEntries.size > 1) {
                                IconButton(
                                    onClick = {
                                        parentBackstackState.mutate {
                                            dropLastWhile { it.destination == this@NestedBackstackExampleDestination }
                                        }
                                    }
                                ) {
                                    Icon(Sharp.ArrowBack, contentDescription = "Go back")
                                }
                            }
                        },
                        title = {
                            Column {
                                Text("Nested Backstack")
                                AnimatedVisibility(visible = nestedEntries.size > 1) {
                                    Text(
                                        "Depth: ${nestedEntries.size}",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        },
                    )
                },
            ) { contentPadding ->
                Box(
                    modifier = Modifier.padding(contentPadding),
                ) {
                    BackstackContext(
                        backstackState = nestedBackstackState,
                    ) {
                        AnimatedContent(
                            targetState = nestedEntries.last(),
                            label = "nested-in-scaffold-transition",
                        ) { entry ->
                            entry.Render()
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun NestedBackstackExampleDestinationPreview() {
    KruiserSampleTheme {
        NestedBackstackExampleDestination.Preview()
    }
}

@Preview
@Composable
fun NestedBackstackExampleDestinationWithParentEntriesPreview() {
    KruiserSampleTheme {
        NestedBackstackExampleDestination.Preview(
            entriesBefore = listOf(
                BackstackEntry(
                    SimplePageDestination(level = 0),
                    id = "parent-entry-id"
                )
            ),
        )
    }
}

