package de.gaw.kruiser.example.tab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.ui.rendering.Render
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Preview
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.example.EmojiDestination
import de.gaw.kruiser.example.bottomsheet.BottomSheetRendererDestination
import de.gaw.kruiser.example.emojis
import de.gaw.kruiser.ui.theme.KruiserSampleTheme
import kotlinx.parcelize.Parcelize

val tabs = emojis.map { emoji ->
    BackstackEntry(EmojiDestination(emoji))
}

@Parcelize
object TabDestination : AndroidDestination {
    private fun readResolve(): Any = TabDestination

    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() {
            var currentTab: BackstackEntry by rememberSaveable { mutableStateOf(tabs.first()) }
            Scaffold(
                modifier = Modifier
                    .navigationBarsPadding(),
                bottomBar = {
                    Surface {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            tabs.forEach {
                                Tab(
                                    modifier = Modifier.weight(1f),
                                    selected = currentTab == it,
                                    onClick = { currentTab = it },
                                    text = {
                                        Text(
                                            (it.destination as? EmojiDestination)?.emoji
                                                ?: "Not an Emoji Destination"
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    currentTab.Render()
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

