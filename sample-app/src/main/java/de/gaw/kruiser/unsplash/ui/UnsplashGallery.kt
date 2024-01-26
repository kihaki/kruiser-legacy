package de.gaw.kruiser.unsplash.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import de.gaw.kruiser.unsplash.destination.UnsplashImageDestination
import de.gaw.kruiser.backstack.MutableBackstack
import de.gaw.kruiser.backstack.currentEntries
import de.gaw.kruiser.backstack.push
import de.gaw.kruiser.backstack.ui.BackstackContent
import de.gaw.kruiser.backstack.ui.animation.CardstackAnimation
import de.gaw.kruiser.backstack.ui.animation.DoubleCardstackAnimation
import de.gaw.kruiser.imagesCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Composable
fun UnsplashGallery(
    backstack: MutableBackstack,
    modifier: Modifier = Modifier,
) {
    // Push the first unsplash image once its available
    LaunchedEffect(backstack) {
        withContext(Dispatchers.IO) {
            if (backstack.currentEntries()
                .map { it.destination }
                    .lastOrNull()?.javaClass != UnsplashImageDestination::class.java
            ) {
                imagesCache.filter { it.isNotEmpty() }.first().let {
                    backstack.push(UnsplashImageDestination(0, it.first()))
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier,
    ) {
        val isPortrait = maxWidth <= maxHeight
        BackstackContent(backstack) {
            when (isPortrait) {
                true -> CardstackAnimation()
                false -> DoubleCardstackAnimation()
            }
        }
    }
}