package de.gaw.kruiser.unsplash.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.backstack.push
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.imagesCache
import de.gaw.kruiser.unsplash.ui.ForwardBackwardButtonRow
import de.gaw.kruiser.unsplash.ui.UnsplashGallery
import kotlinx.parcelize.Parcelize

@Parcelize
object SharedUiExampleDestination : AndroidDestination {
    private fun readResolve(): Any = SharedUiExampleDestination

    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() {
            val backstack = rememberSaveableBackstack()

            Surface {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UnsplashGallery(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        backstack = backstack,
                    )

                    val backstackEntries by backstack.collectEntries()
                    val cachedImages by imagesCache.collectAsState()
                    ForwardBackwardButtonRow(
                        backstack = backstack,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .navigationBarsPadding(),
                        forwardEnabled = cachedImages.isNotEmpty(),
                        backwardEnabled = backstackEntries.size > 1,
                        onForwardAction = {
                            val photos = backstackEntries.map { it.destination }
                                .filterIsInstance<UnsplashImageDestination>()
                            val nextIndex = photos.size
                            backstack.push(
                                UnsplashImageDestination(
                                    nextIndex,
                                    imagesCache.value[nextIndex % imagesCache.value.size]
                                )
                            )
                        },
                    )
                }
            }
        }
    }
}