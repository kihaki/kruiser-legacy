package de.gaw.kruiser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import de.gaw.kruiser.backstack.core.MutableBackstack
import de.gaw.kruiser.backstack.core.SavedStateMutableBackstack
import de.gaw.kruiser.backstack.debug.DebugBackstackLoggerEffect
import de.gaw.kruiser.backstack.ui.Backstack
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.example.ExampleListDestination
import de.gaw.kruiser.ui.theme.KruiserSampleTheme

interface MutableBackstackProvider {
    val backstack: MutableBackstack
}

class SavedStateMutableBackstackProvider(
    vararg initial: Destination,
    savedState: SavedStateHandle,
    key: String,
) : MutableBackstackProvider {
    override val backstack = SavedStateMutableBackstack(
        initial = initial.toList(),
        savedStateHandle = savedState,
        stateKey = key,
    )
}

class MasterNavigationStateViewModel(savedState: SavedStateHandle) :
    ViewModel(),
    MutableBackstackProvider by SavedStateMutableBackstackProvider(
        ExampleListDestination,
//        EmojiDestination(emojis.random()),
//        BackstackInScaffoldExampleDestination,
        savedState = savedState,
        key = "nav:master",
    )

@Composable
fun activityViewModelStoreOwner() =
    LocalContext.current as ViewModelStoreOwner

@Composable
fun masterNavigationStateViewModel(): MasterNavigationStateViewModel =
    viewModel(
        viewModelStoreOwner = activityViewModelStoreOwner(),
        factory = viewModelFactory {
            addInitializer(MasterNavigationStateViewModel::class) {
                MasterNavigationStateViewModel(
                    savedState = createSavedStateHandle(),
                )
            }
        },
    )

class MasterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            KruiserSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val masterBackstack = masterNavigationStateViewModel().backstack
                    Backstack(
                        backstack = masterBackstack,
                    )
                    DebugBackstackLoggerEffect(
                        tag = "MasterBackstack",
                        backstack = masterBackstack,
                    )
                }
            }
        }
    }
}
