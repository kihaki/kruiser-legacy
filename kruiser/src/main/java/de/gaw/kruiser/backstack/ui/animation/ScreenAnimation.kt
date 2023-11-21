package de.gaw.kruiser.backstack.ui.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.Entries
import de.gaw.kruiser.backstack.ui.animation.util.LocalAnimationSyncedEntries
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.viewmodel.destinationViewModelStoreOwner
import kotlinx.coroutines.flow.collectLatest

interface ScreenAnimationContext {
    val backstack: Backstack
    val animationSyncedEntries: Entries
}

private data class ScreenAnimationContextImpl(
    override val backstack: Backstack,
    override val animationSyncedEntries: Entries,
) : ScreenAnimationContext

typealias ScreenAnimationSpec = AnimatedContentTransitionScope<Pair<Destination, Int>?>.(ScreenAnimationContext) -> ContentTransform

@Composable
fun ScreenAnimation(
    backstack: Backstack,
    modifier: Modifier = Modifier,
    animationSpec: ScreenAnimationSpec,
) {
    val entries by backstack.collectEntries()
    val animationSyncedEntriesState = remember { mutableStateOf(entries) }
    var animationSyncedEntries by animationSyncedEntriesState

    fun updateAnimatedBackstack() {
        animationSyncedEntries = entries
    }

    LaunchedEffect(backstack) {
        backstack.entries.collectLatest {
            if (it.size > animationSyncedEntries.size) {
                updateAnimatedBackstack() // Update instantly on push
            }
        }
    }

    CompositionLocalProvider(LocalAnimationSyncedEntries provides animationSyncedEntriesState) {
        AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier),
            targetState = entries.lastOrNull()?.let { it to entries.lastIndex },
            transitionSpec = {
                animationSpec(
                    this,
                    ScreenAnimationContextImpl(
                        backstack = backstack,
                        animationSyncedEntries = animationSyncedEntries
                    )
                )
            },
            label = "cardstack-animation"
        ) { spec ->
            when (spec) {
                null -> Spacer(modifier = modifier.fillMaxSize())
                else -> {
                    val (destination, index) = spec
                    val viewModelStoreOwner = destinationViewModelStoreOwner(destination) {
                        !entries.contains(destination) || index > entries.lastIndex
                    }

                    val screen = remember { destination.build() }
                    CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
                        screen.Content()
                    }

                    DisposableEffect(Unit) {
                        onDispose {
                            if (!entries.contains(destination) || index > entries.lastIndex) {
                                updateAnimatedBackstack() // Update delayed on pop
                            }
                        }
                    }
                }
            }
        }
    }
}

val cardStackAnimationSpec: ScreenAnimationSpec = { context ->
    fun entries() = context.backstack.entries.value
    fun Int.slideOutFraction() = (this * .1f).toInt()
    fun isPushing() = entries().size >= context.animationSyncedEntries.size
    (when (isPushing()) {
        true -> slideInHorizontally { it }
        false -> slideInHorizontally { (-it).slideOutFraction() }
    } togetherWith when (isPushing()) {
        true -> slideOutHorizontally { (-it).slideOutFraction() }
        false -> slideOutHorizontally { it }
    }).apply {
        targetContentZIndex = when (isPushing()) {
            true -> entries().size
            false -> entries().size - 1
        }.toFloat()
    }
}