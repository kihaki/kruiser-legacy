package de.gaw.kruiser.example

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.ui.Backstack
import de.gaw.kruiser.backstack.ui.rendering.Render
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
object BackstackInScaffoldExampleDestination : AndroidDestination {
    private fun readResolve(): Any = BackstackInScaffoldExampleDestination

    @OptIn(ExperimentalMaterial3Api::class)
    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() {
            val backstack =
                rememberSaveableBackstack("in-scaffold", EmojiDestination(emojis.random()))
            Scaffold(
                topBar = { TopAppBar(title = { Text("Nested Example") }) },
                bottomBar = { BottomAppBar { } },
            ) {
                Box(
                    modifier = Modifier.padding(it),
                ) {
                    Backstack(
                        state = backstack,
                    ) {
                        val entries by it.collectEntries()
                        AnimatedContent(
                            targetState = entries.lastOrNull(),
                            label = "backstackInScaffold-transition",
                        ) {
                            it?.Render()
                        }
                    }
                }
            }
        }
    }
}
