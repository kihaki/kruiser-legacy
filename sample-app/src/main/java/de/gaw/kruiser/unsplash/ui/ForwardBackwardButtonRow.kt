package de.gaw.kruiser.unsplash.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.MutableBackstack
import de.gaw.kruiser.backstack.pop
import de.gaw.kruiser.backstack.ui.util.collectEntries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardBackwardButtonRow(
    backstack: Backstack,
    modifier: Modifier = Modifier,
    forwardEnabled: Boolean = true,
    backwardEnabled: Boolean = true,
    onBackwardAction: () -> Unit = { (backstack as? MutableBackstack)?.pop() },
    onForwardAction: () -> Unit,
) {
    val entries by backstack.collectEntries()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        horizontalArrangement = Arrangement.Center,
    ) {
        AnimatedContent(
            modifier = Modifier
                .height(intrinsicSize = IntrinsicSize.Max),
            targetState = entries.count() > 1,
            label = "back-buttons-transition",
            transitionSpec = {
                slideInHorizontally { it } togetherWith slideOutHorizontally { it }
            }
        ) { showButtons ->
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(intrinsicSize = IntrinsicSize.Max),
            ) {
                Spacer(modifier = Modifier.size(24.dp))
                if (showButtons) {
                    ElevatedCard(
                        modifier = Modifier.padding(16.dp),
                        shape = RoundedCornerShape(50),
                        enabled = backwardEnabled,
                        onClick = onBackwardAction,
                    ) {
                        Icon(
                            modifier = Modifier.size(96.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "go back",
                        )
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
        }
        ElevatedCard(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(50),
            enabled = forwardEnabled,
            onClick = onForwardAction,
        ) {
            Icon(
                modifier = Modifier.size(96.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "go further",
            )
        }
        Spacer(modifier = Modifier.size(24.dp))
    }
}