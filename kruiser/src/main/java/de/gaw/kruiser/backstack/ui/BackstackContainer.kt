package de.gaw.kruiser.backstack.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.MutableBackstack
import de.gaw.kruiser.backstack.pop
import de.gaw.kruiser.backstack.ui.animation.CardstackAnimation
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack

/**
 * Handles back presses and sets the [LocalBackstack] defers rendering to the [content] composable.
 * By default a [CardstackAnimation] is used for rendering.
 */
@Composable
fun BackstackContainer(
    backstack: MutableBackstack = rememberSaveableBackstack(),
    content: @Composable (Backstack) -> Unit = { stack -> CardstackAnimation(stack) },
) {
    val entries by backstack.collectEntries()

    BackHandler(
        enabled = entries.size > 1,
        onBack = backstack::pop,
    )

    CompositionLocalProvider(
        LocalBackstack provides backstack,
    ) {
        content(backstack)
    }
}


