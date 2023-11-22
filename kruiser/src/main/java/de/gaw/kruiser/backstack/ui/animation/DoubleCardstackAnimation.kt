package de.gaw.kruiser.backstack.ui.animation

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.util.rememberDerivedBackstackOf

/**
 * Renders screens as if they were a stack of cards.
 */
@Composable
fun DoubleCardstackAnimation(
    backstack: Backstack = LocalBackstack.current,
) {
    val offsetBackstack = rememberDerivedBackstackOf(backstack) { dropLast(1) }
    Row {
        CardstackAnimation(modifier = Modifier.weight(1f), backstack = offsetBackstack)
        CardstackAnimation(modifier = Modifier.weight(1f), backstack = backstack)
    }
}