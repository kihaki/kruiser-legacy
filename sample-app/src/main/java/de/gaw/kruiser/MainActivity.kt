package de.gaw.kruiser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.gaw.kruiser.backstack.pushAvoidDuplicates
import de.gaw.kruiser.backstack.ui.BackstackContainer
import de.gaw.kruiser.backstack.ui.animation.CardstackAnimation
import de.gaw.kruiser.backstack.ui.animation.DoubleCardstackAnimation
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.ui.theme.KruiserSampleTheme
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
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.Serializable

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
var imagesCache = listOf<String>()
private const val UNSPLASH_ACCESS = BuildConfig.UNSPLASH_KEY
fun addToCache() = MainScope().launch {
    val response =
        httpClient.get("https://api.unsplash.com/photos/random?client_id=$UNSPLASH_ACCESS&count=30")
    val body: List<UnsplashPhoto> = response.body()
    imagesCache += body.mapNotNull { it.urls?.regular }
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
                    val backstack = rememberSaveableBackstack(listOf(Dashboard()))
                    BoxWithConstraints {
                        val isPortrait = maxWidth <= maxHeight
                        BackstackContainer(backstack) {
                            when (isPortrait) {
                                true -> CardstackAnimation(backstack)
                                false -> DoubleCardstackAnimation(backstack)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Dashboard(val index: Int = 0, val url: String? = null) : Destination, Serializable {
    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() {
            Surface {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(url)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                    )
                    val backstack = LocalBackstack.current
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.weight(2f))
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            LaunchedEffect(Unit) {
                                if (imagesCache.size < (index + 5)) {
                                    addToCache()
                                }
                            }
                            Button(onClick = { backstack.mutate { dropLast(size / 2) } }) {
                                Text("Pop half")
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                            Button(onClick = {
                                backstack.pushAvoidDuplicates(
                                    Dashboard(
                                        index = index + 1,
                                        url = imagesCache[index + 1]
                                    )
                                )
                            }) {
                                Text("Next")
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}