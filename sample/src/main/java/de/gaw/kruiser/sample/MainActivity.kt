package de.gaw.kruiser.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import de.gaw.kruiser.Navigation
import de.gaw.kruiser.android.navigationOwnerViewModel
import de.gaw.kruiser.sample.samples.DashboardDestination
import de.gaw.kruiser.sample.theme.KruiserTheme
import de.gaw.kruiser.state.currentStack
import de.gaw.kruiser.state.push

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KruiserTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navigationViewModel = navigationOwnerViewModel().apply {
                        if (state.currentStack.isEmpty()) state.push(DashboardDestination)
                    }
                    Navigation(
                        state = navigationViewModel.state,
                        serviceProvider = navigationViewModel.serviceProvider,
                    )
                }
            }
        }
    }
}