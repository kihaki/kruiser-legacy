package de.gaw.kruiser.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.backstack.push
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstackState
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Preview
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.ui.theme.KruiserSampleTheme
import kotlinx.parcelize.Parcelize
import java.util.UUID

enum class Decoration(val emoji: String) {
    Sunglasses("\uD83D\uDE0E"),
    Fire("\uD83D\uDD25"),
    Rofl("\uD83E\uDD23"),
    Tongue("\uD83D\uDE1D"),
}

@Parcelize
data class SimplePageDestination(val level: Int) : AndroidDestination {
    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() {
            val backstack = LocalMutableBackstackState.currentOrThrow
            Scaffold { contentPadding ->
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .imePadding()
                        .navigationBarsPadding()
                        .statusBarsPadding()
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val decoration = rememberSaveable {
                        Decoration.entries.let { decorations ->
                            decorations[level % decorations.size]
                        }
                    }

                    Spacer(modifier = Modifier.weight(1.5f))

                    val headlineStyle = MaterialTheme.typography.headlineLarge
                    Text(text = decoration.emoji, style = headlineStyle)
                    Text(text = "$decoration", style = headlineStyle)

                    Spacer(modifier = Modifier.size(32.dp))

                    // For verifying BackstackEntry hash stability
                    val currentEntryId = LocalBackstackEntry.currentOrThrow.id.takeLast(5)
                    Text(text = "BackstackEntry Id: $currentEntryId")

                    // For verifying state saving
                    val randomHash = rememberSaveable { randomHash() }
                    Text(text = "Saved: $randomHash")

                    Spacer(modifier = Modifier.size(8.dp))

                    // Button to got to one level deeper
                    val nextLevel = level + 1
                    val nextDecoration = rememberSaveable {
                        Decoration.entries.let { decorations ->
                            decorations[nextLevel % decorations.size]
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    ElevatedButton(
                        onClick = { backstack.push(SimplePageDestination(level = nextLevel)) },
                    ) {
                        Text("Go to ${nextDecoration.emoji}")
                    }

                    Spacer(modifier = Modifier.weight(0.5f))
                }
            }
        }
    }
}

private fun randomHash(length: Int = 5) = UUID.randomUUID().toString().takeLast(length)

@Preview
@Composable
fun SimplePageDestinationPreview() {
    KruiserSampleTheme {
        SimplePageDestination(1).Preview()
    }
}