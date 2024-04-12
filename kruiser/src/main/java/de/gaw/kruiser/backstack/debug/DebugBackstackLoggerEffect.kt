package de.gaw.kruiser.backstack.debug

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.ui.util.LocalBackstackState
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

/**
 * A debug effect that logs the current backstack entries whenever the backstack changes.
 */
@Composable
fun DebugBackstackLoggerEffect(
    tag: String? = null,
    backstack: BackstackState = LocalBackstackState.currentOrThrow,
) {
    val currentTag by rememberUpdatedState(tag)
    LaunchedEffect(tag, backstack) {
        withContext(Dispatchers.IO) {
            backstack.entries.collectLatest {
                Log.v(
                    "BackstackUpdate",
                    "\n--- ${currentTag ?: "Backstack"} ${backstack.id} has changed: ---",
                )
                it.forEachIndexed { index, backstackEntry ->
                    Log.v("BackstackUpdate", "   [$index]: $backstackEntry")
                }
            }
        }
    }
}