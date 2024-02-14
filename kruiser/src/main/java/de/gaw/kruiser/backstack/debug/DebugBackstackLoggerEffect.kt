package de.gaw.kruiser.backstack.debug

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import de.gaw.kruiser.backstack.core.Backstack
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import java.time.LocalTime

@Composable
fun DebugBackstackLoggerEffect(
    tag: String? = null,
    backstack: Backstack = LocalBackstack.currentOrThrow,
) {
    val currentTag by rememberUpdatedState(tag)
    LaunchedEffect(tag, backstack) {
        withContext(Dispatchers.IO) {
            backstack.entries.collectLatest {
                val currentTime = LocalTime.now()
                Log.v("BackstackUpdate", "")
                Log.v(
                    "BackstackUpdate",
                    "${currentTag ?: "Backstack"} ${backstack.id} has changed at $currentTime:",
                )
                it.forEachIndexed { index, backstackEntry ->
                    Log.v("BackstackUpdate", "   [$index]: $backstackEntry")
                }
            }
        }
    }
}