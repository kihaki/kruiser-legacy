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
import de.gaw.kruiser.backstack.debug.DebugBackstackLoggerEffect
import de.gaw.kruiser.backstack.savedstate.PersistedMutableBackstack
import de.gaw.kruiser.backstack.ui.Backstack
import de.gaw.kruiser.example.ExamplesListDestination
import de.gaw.kruiser.ui.theme.KruiserSampleTheme

class MasterNavigationViewModel(savedState: SavedStateHandle) : ViewModel() {
    val backstack = savedState.PersistedMutableBackstack(
        id = "nav:master",
        initial = listOf(
            ExamplesListDestination,
        ),
    )
}

@Composable
fun activityViewModelStoreOwner() =
    LocalContext.current as ViewModelStoreOwner

@Composable
fun masterNavigationStateViewModel(): MasterNavigationViewModel =
    viewModel(
        viewModelStoreOwner = activityViewModelStoreOwner(),
        factory = viewModelFactory {
            addInitializer(MasterNavigationViewModel::class) {
                MasterNavigationViewModel(createSavedStateHandle())
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
