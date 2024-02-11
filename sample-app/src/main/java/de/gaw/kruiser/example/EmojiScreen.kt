package de.gaw.kruiser.example

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gaw.kruiser.backstack.push
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.util.rememberIsOnBackstack
import de.gaw.kruiser.backstack.ui.transition.AnimatedTransition
import de.gaw.kruiser.backstack.ui.transition.CardTransition
import de.gaw.kruiser.backstack.ui.transparency.TransparentScreen
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstack
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.parcelize.Parcelize
import java.util.UUID

val emojis = persistentListOf(
    "\uD83D\uDE0E", // Sunglasses
    "\uD83D\uDD25", // Fire
    "\uD83E\uDEE0", // Melt
    "\uD83E\uDD23", // Rofl
    "\uD83D\uDE1D", // Tongue
)

@Parcelize
data class EmojiDestination(val emoji: String) : AndroidDestination {
    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() = CardTransition {
            EmojiCard(emoji)
        }
    }
}

class EmojiViewModel(private val emoji: String) : ViewModel() {
    data class Factory(val emoji: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return EmojiViewModel(emoji) as T
        }
    }

    init {
        Log.v("EmojiViewModel", "Init VM for $emoji (${hashCode()})")
    }

    override fun onCleared() {
        Log.v("EmojiViewModel", "Disposing VM for $emoji (${hashCode()})")
        super.onCleared()
    }
}

@Composable
private fun EmojiCard(emoji: String) {
    val backstack = LocalMutableBackstack.currentOrThrow

    @Suppress("UNUSED_VARIABLE")
    val viewModel =
        viewModel<EmojiViewModel>(factory = remember(emoji) { EmojiViewModel.Factory(emoji) })
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = emoji, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = LocalBackstackEntry.currentOrThrow.id.takeLast(5),
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.size(16.dp))
            val randomHash = rememberSaveable {
                UUID.randomUUID().toString().takeLast(5)
            }
            Text(text = randomHash, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.size(16.dp))
            ElevatedButton(
                onClick = {
                    backstack.push(EmojiDestination(emojis.filterNot { it == emoji }.random()))
                },
            ) {
                Text("Next")
            }
            Spacer(modifier = Modifier.size(16.dp))
            ElevatedButton(
                onClick = {
                    backstack.push(BottomSheetDestination)
                },
            ) {
                Text("Show Bottom Sheet")
            }
        }
    }
}

@Parcelize
object BottomSheetDestination : AndroidDestination {
    private fun readResolve(): Any = BottomSheetDestination

    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() = TransparentScreen {
            val isOnBackstack by rememberIsOnBackstack()

            val isBackgroundScrimmed by rememberSaveable {
                mutableStateOf(false)
            }.apply {
                value = isOnBackstack
            }
            val backgroundTransparency by animateFloatAsState(
                if (isBackgroundScrimmed) .3f else .0f,
                label = "bottom-sheet-background-scrim",
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = backgroundTransparency)),
                contentAlignment = Alignment.BottomCenter,
            ) {
                AnimatedTransition(
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it },
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                    ) {
                        Box(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .padding(16.dp)
                        ) {
                            Text(text = "Yes, hello, this is BottomSheet")
                        }
                    }
                }
            }
        }
    }
}