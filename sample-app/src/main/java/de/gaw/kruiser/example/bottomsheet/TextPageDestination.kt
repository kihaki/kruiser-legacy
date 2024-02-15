package de.gaw.kruiser.example.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.backstack.push
import de.gaw.kruiser.backstack.ui.transition.CardTransition
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstack
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import kotlinx.parcelize.Parcelize
import java.util.UUID

interface TextPageDestination : AndroidDestination {
    val title: String
    val contentModifier: Modifier get() = Modifier.fillMaxSize()

    @Composable
    fun Decoration(content: @Composable () -> Unit) = content()

    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() = Decoration {
            val backstack = LocalMutableBackstack.currentOrThrow
            Surface(
                shadowElevation = 4.dp,
            ) {
                Box(
                    modifier = contentModifier
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.displayLarge,
                        )
                        Text(
                            text = rememberSaveable {
                                UUID.randomUUID().toString().takeLast(5)
                            }
                        )
                        ElevatedButton(
                            onClick = {
                                backstack.push(RegularPageDestination("Page at position ${backstack.entries.value.size}"))
                            },
                        ) {
                            Text("Push Regular")
                        }
                        ElevatedButton(
                            onClick = {
                                backstack.push(BottomSheetPageDestination("Bottomsheet at position ${backstack.entries.value.size}"))
                            },
                        ) {
                            Text("Push BottomSheet")
                        }
                    }
                }
            }
        }
    }
}

@Parcelize
data class RegularPageDestination(
    override val title: String,
) : TextPageDestination {
    @Composable
    override fun Decoration(content: @Composable () -> Unit) = CardTransition {
        content()
    }
}

@Parcelize
data class BottomSheetPageDestination(
    override val title: String,
) : TextPageDestination,
    BottomSheetDestination {
    override val contentModifier
        get() = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
}
