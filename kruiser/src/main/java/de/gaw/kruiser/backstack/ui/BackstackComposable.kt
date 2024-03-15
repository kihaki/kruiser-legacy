package de.gaw.kruiser.backstack.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.core.MutableBackstackState
import de.gaw.kruiser.backstack.pop
import de.gaw.kruiser.backstack.ui.rendering.BackstackRenderer
import de.gaw.kruiser.backstack.ui.util.LocalBackstackState
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstackState
import de.gaw.kruiser.backstack.ui.util.LocalSaveableStateHolder
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.currentOrThrow

@Composable
fun Backstack(
    backstack: MutableBackstackState,
    modifier: Modifier = Modifier,
    stateHolder: SaveableStateHolder = LocalSaveableStateHolder.current
        ?: rememberSaveableStateHolder(),
) {
    BackstackContext(
        mutableBackstack = backstack,
        stateHolder = stateHolder,
    ) {
        BackstackRenderer(
            modifier = modifier,
            backstack = backstack,
        )
    }
}

/**
 * Handles back presses and sets the [LocalMutableBackstackState] defers rendering to the [content] composable.
 */
@Composable
fun BackstackContext(
    mutableBackstack: MutableBackstackState = LocalMutableBackstackState.currentOrThrow,
    backstack: BackstackState = mutableBackstack,
    stateHolder: SaveableStateHolder = LocalSaveableStateHolder.current
        ?: rememberSaveableStateHolder(),
    content: @Composable (BackstackState) -> Unit,
) {
    CompositionLocalProvider(
        LocalMutableBackstackState provides mutableBackstack,
        LocalBackstackState provides backstack,
        LocalSaveableStateHolder provides stateHolder,
    ) {
        stateHolder.SaveableStateProvider(key = "bs:${backstack.id}") {
            val entries by mutableBackstack.collectEntries()

            BackHandler(
                enabled = entries.size > 1,
                onBack = mutableBackstack::pop,
            )

            content(backstack)
        }
    }
}


