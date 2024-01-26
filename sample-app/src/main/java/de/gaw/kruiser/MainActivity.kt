package de.gaw.kruiser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.MutableBackstack
import de.gaw.kruiser.backstack.ui.BackstackContent
import de.gaw.kruiser.backstack.ui.animation.CardstackAnimation
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack
import de.gaw.kruiser.ui.theme.KruiserSampleTheme
import de.gaw.kruiser.unsplash.destination.SharedUiExampleDestination
import de.gaw.kruiser.unsplash.model.UnsplashPhoto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private val httpClient = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(
            Json {
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
    install(Logging) {
        level = LogLevel.ALL
    }
}
var imagesCache = MutableStateFlow(listOf<String>())
private val UNSPLASH_ACCESS = BuildConfig.UNSPLASH_KEY
fun addToCache() = MainScope().launch {
    val response =
        httpClient.get("https://api.unsplash.com/photos/random?client_id=$UNSPLASH_ACCESS&count=30")
    val body: List<UnsplashPhoto> = response.body()
    imagesCache.update { it + body.mapNotNull { items -> items.urls?.regular } }
}

@Composable
fun Cardstack(backstack: MutableBackstack) {
    BackstackContent(backstack) {
        CardstackAnimation()
    }
}

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
                    LaunchedEffect(Unit) {
                        addToCache()
                    }
                    val backstack = rememberSaveableBackstack(SharedUiExampleDestination)
                    Cardstack(backstack)
                }
            }
        }
    }
}

