package de.gaw.kruiser.example.tab

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.gaw.kruiser.backstack.ui.rendering.Render
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Preview
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.example.Decoration
import de.gaw.kruiser.example.SimplePageDestination
import de.gaw.kruiser.tab.rememberTabDestinationsState
import de.gaw.kruiser.tab.setCurrent
import de.gaw.kruiser.ui.theme.KruiserSampleTheme
import kotlinx.parcelize.Parcelize

@Parcelize
object TabDestination : AndroidDestination {
    private fun readResolve(): Any = TabDestination

    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() {
            val tabsState = rememberTabDestinationsState(
                destinations = List(Decoration.entries.size) { index ->
                    SimplePageDestination(index)
                },
            )
            val currentTab by tabsState.current.collectAsState()
            val tabs by tabsState.entries.collectAsState()
            Scaffold(
                modifier = Modifier
                    .navigationBarsPadding(),
                bottomBar = {
                    Surface {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            tabs.forEachIndexed { index, tab ->
                                Tab(
                                    modifier = Modifier.weight(1f),
                                    selected = currentTab == tab,
                                    onClick = { tabsState.setCurrent(tab) },
                                    text = {
                                        Text(Decoration.entries[index].emoji)
                                    }
                                )
                            }
                        }
                    }
                }
            ) { padding ->
                AnimatedContent(
                    modifier = Modifier.padding(padding),
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "tab-transition",
                ) { entry ->
                    entry.Render()
                }
            }
        }
    }
}

@Preview
@Composable
private fun TabDestinationPreview() = KruiserSampleTheme {
    TabDestination.Preview()
}

