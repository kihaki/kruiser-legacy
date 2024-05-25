package de.gaw.kruiser.example.tab

import android.os.Parcelable
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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.core.BackstackEntryId
import de.gaw.kruiser.backstack.core.generateId
import de.gaw.kruiser.backstack.ui.rendering.Render
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.destination.Preview
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.example.EmojiDestination
import de.gaw.kruiser.example.emojis
import de.gaw.kruiser.ui.theme.KruiserSampleTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize

@Parcelize
private data class SavedTabDestinationsState(
    val current: BackstackEntry,
    val entries: BackstackEntries,
) : Parcelable

private val TabDestinationsStateSaver = Saver<TabDestinationsState, SavedTabDestinationsState>(
    save = { state ->
        SavedTabDestinationsState(
            current = state.current.value,
            entries = state.entries.value,
        )
    },
    restore = { saved ->
        DefaultTabDestinationsState(
            entries = saved.entries,
            initial = saved.current,
        )
    },
)

interface TabDestinationsState {
    val current: StateFlow<BackstackEntry>
    val entries: StateFlow<List<BackstackEntry>>

    fun setCurrent(entry: BackstackEntry)
}

class DefaultTabDestinationsState(
    entries: List<BackstackEntry>,
    initial: BackstackEntry = entries.first(),
) : TabDestinationsState {
    constructor(
        destinations: List<Destination>,
        initial: Destination = destinations.first(),
        generateId: (Destination) -> BackstackEntryId = { BackstackEntry.generateId() },
    ) : this(
        entries = destinations.map { BackstackEntry(destination = it, id = generateId(it)) },
        initial = BackstackEntry(destination = initial, id = generateId(initial))
    )

    override val entries = MutableStateFlow(entries)
    override val current = MutableStateFlow(initial)

    override fun setCurrent(entry: BackstackEntry) {
        current.update { entry }
    }
}

@Composable
fun rememberTabDestinationsState(
    destinations: List<Destination>,
    initial: Destination = destinations.first(),
): TabDestinationsState = rememberSaveable(
    saver = TabDestinationsStateSaver,
) {
    DefaultTabDestinationsState(destinations, initial)
}

@Parcelize
object TabDestination : AndroidDestination {
    private fun readResolve(): Any = TabDestination

    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() {
            val tabsState = rememberTabDestinationsState(
                destinations = emojis.map { emoji -> EmojiDestination(emoji) },
            )
            val currentTab by tabsState.current.collectAsState()
            val tabs by tabsState.entries.collectAsState()
            Scaffold(
                modifier = Modifier
                    .navigationBarsPadding(),
                bottomBar = {
                    Surface {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            tabs.forEach { tab ->
                                Tab(
                                    modifier = Modifier.weight(1f),
                                    selected = currentTab == tab,
                                    onClick = { tabsState.setCurrent(tab) },
                                    text = {
                                        Text(
                                            (tab.destination as? EmojiDestination)?.emoji
                                                ?: "Not an Emoji Destination"
                                        )
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

