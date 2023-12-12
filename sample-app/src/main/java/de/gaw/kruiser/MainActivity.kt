package de.gaw.kruiser

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.gaw.kruiser.backstack.MutableBackstack
import de.gaw.kruiser.backstack.currentEntries
import de.gaw.kruiser.backstack.push
import de.gaw.kruiser.backstack.ui.BackstackContent
import de.gaw.kruiser.backstack.ui.animation.CardstackAnimation
import de.gaw.kruiser.backstack.ui.animation.DoubleCardstackAnimation
import de.gaw.kruiser.backstack.ui.util.collectEntries
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.Serializable
import java.util.UUID

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
                    val backstack = rememberSaveableBackstack(DashboardWizard)
                    Cardstack(backstack)
                }
            }
        }
    }
}

object DashboardWizard : Destination, Serializable {
    private fun readResolve(): Any = DashboardWizard

    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() {
            val backstack = rememberSaveableBackstack()

            // Push the first unsplash image once its available
            LaunchedEffect(backstack) {
                if (backstack.currentEntries()
                        .lastOrNull()?.javaClass != UnsplashImage::class.java
                ) {
                    imagesCache.filter { it.isNotEmpty() }.first().let {
                        backstack.push(UnsplashImage(0, it.first()))
                    }
                }
            }

            val entries by backstack.collectEntries()
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                val isPortrait = maxWidth <= maxHeight
                BackstackContent(backstack) {
                    when (isPortrait) {
                        true -> CardstackAnimation()
                        false -> DoubleCardstackAnimation()
                    }
                }

                Row(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black,
                                )
                            )
                        )
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                ) {
                    AnimatedContent(
                        modifier = Modifier
                            .height(intrinsicSize = IntrinsicSize.Max),
                        targetState = entries.count() > 1,
                        label = "back-buttons-transition",
                        transitionSpec = {
                            slideInHorizontally { -it } togetherWith slideOutHorizontally { -it }
                        }
                    ) { showButtons ->
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(intrinsicSize = IntrinsicSize.Max),
                        ) {
                            Spacer(modifier = Modifier.size(24.dp))
                            if (showButtons) {
                                Button(
                                    onClick = { backstack.mutate { dropLast(1) } }) {
                                    Text("< Back")
                                }
                                Spacer(modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    val cachedImages by imagesCache.collectAsState()
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = cachedImages.isNotEmpty(),
                        onClick = {
                            val photos =
                                backstack.currentEntries().filterIsInstance<UnsplashImage>()
                            val nextIndex = photos.size
                            backstack.push(
                                UnsplashImage(
                                    nextIndex,
                                    imagesCache.value[nextIndex % imagesCache.value.size]
                                )
                            )
                        }
                    ) {
                        Text(
                            when (cachedImages.isNotEmpty()) {
                                true -> "Next"
                                false -> "Loading..."
                            }
                        )
                    }
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

data class UnsplashImage(val index: Int = 0, val url: String) : Destination, Serializable {
    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() {
            @Suppress("UNUSED_VARIABLE")
            val viewModel = viewModel<UnsplashImageScreenViewModel>(
                factory = UnsplashImageScreenViewModel.Factory(url = url)
            )

            val someId = rememberSaveable { UUID.randomUUID().toString().take(5) }

            Surface {
                Box(contentAlignment = Alignment.Center) {

                    LaunchedEffect(Unit) {
                        if (imagesCache.value.size < (index + 5)) {
                            addToCache()
                        }
                    }
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
                    Text(
                        someId,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.background(Color.White)
                    )
                }
            }
        }
    }
}

class UnsplashImageScreenViewModel(private val url: String) : ViewModel() {
    data class Factory(private val url: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return UnsplashImageScreenViewModel(url = url) as T
        }
    }

    init {
        Log.v("UnsplashViewModel", "Init: $url")
    }

    override fun onCleared() {
        super.onCleared()
        Log.v("UnsplashViewModel", "Disposed: $url")
    }
}