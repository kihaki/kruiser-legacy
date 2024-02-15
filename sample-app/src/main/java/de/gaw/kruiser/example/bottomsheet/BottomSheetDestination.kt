package de.gaw.kruiser.example.bottomsheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.backstack.core.MutableBackstack
import de.gaw.kruiser.backstack.ui.BackstackContext
import de.gaw.kruiser.backstack.ui.rendering.BackstackRenderer
import de.gaw.kruiser.backstack.ui.rendering.Render
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.util.rememberDerivedBackstackOf
import de.gaw.kruiser.destination.Destination

interface BottomSheetDestination : Destination

@Composable
fun BackstackRendererWithBottomSheet(
    modifier: Modifier = Modifier,
    mutableBackstack: MutableBackstack,
) {
    val entries by mutableBackstack.collectEntries()
    Box(modifier = modifier) {
        // Regular screens
        val nonBottomSheetBackstack = rememberDerivedBackstackOf(mutableBackstack) {
            filter { it.destination !is BottomSheetDestination }
        }
        BackstackContext(
            mutableBackstack = mutableBackstack,
            backstack = nonBottomSheetBackstack,
        ) {
            BackstackRenderer(backstack = it)
        }

        // Bottomsheet Screens
        val bottomSheetBackstack = rememberDerivedBackstackOf(mutableBackstack) {
            filter { it.destination is BottomSheetDestination }
        }
        val isBottomSheetVisible by remember { derivedStateOf { entries.lastOrNull()?.destination is BottomSheetDestination } }
        val backgroundScrim by animateFloatAsState(
            if (isBottomSheetVisible) .5f else 0f,
            label = "sheet-background-scrim-anim"
        )
        Box(
            modifier = Modifier
                .background(color = Color.Black.copy(alpha = backgroundScrim))
                .fillMaxSize(),
        )
        AnimatedVisibility(
            visible = isBottomSheetVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .wrapContentHeight()
                .fillMaxWidth()
                .animateContentSize(),
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
        ) {
            Surface(
                shadowElevation = 4.dp,
            ) {
                BackstackContext(
                    mutableBackstack = mutableBackstack,
                    backstack = bottomSheetBackstack,
                ) { sheetStack ->
                    val sheetEntries by sheetStack.collectEntries()
                    // Render bottom sheet content
                    AnimatedContent(
                        modifier = Modifier.navigationBarsPadding(),
                        targetState = sheetEntries.lastOrNull(),
                        transitionSpec = {
                            slideInHorizontally { it } togetherWith slideOutHorizontally { -it / 2 }
                        },
                        label = "bottom-sheet-screen-transition",
                    ) { entry ->
                        entry?.Render()
                    }
                }
            }
        }
    }
}
