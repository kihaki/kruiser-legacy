package de.gaw.kruiser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.ui.Backstack
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack
import de.gaw.kruiser.example.BackstackInScaffoldExampleDestination
import de.gaw.kruiser.example.EmojiDestination
import de.gaw.kruiser.example.emojis
import de.gaw.kruiser.ui.theme.KruiserSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            KruiserSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val backstack = rememberSaveableBackstack(
                        listOf(
//                            NestedBackstackExampleDestination,
                            BackstackInScaffoldExampleDestination,
                            BackstackInScaffoldExampleDestination,
                            EmojiDestination(emojis.first()),
                            EmojiDestination(emojis.drop(1).first()),
                            EmojiDestination(emojis.drop(2).first()),
                        )
                    )

                    Backstack(
                        backstack = backstack,
                    )
                }
            }
        }
    }
}

