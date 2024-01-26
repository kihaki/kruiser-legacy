package de.gaw.kruiser.unsplash.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.gaw.kruiser.addToCache
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.imagesCache
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class UnsplashImageDestination(val index: Int = 0, val url: String) : AndroidDestination {
    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() {
            @Suppress("UNUSED_VARIABLE")
            val viewModel = viewModel<UnsplashImageScreenViewModel>(
                factory = remember(url) { UnsplashImageScreenViewModel.Factory(url = url) }
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
                            .crossfade(false)
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