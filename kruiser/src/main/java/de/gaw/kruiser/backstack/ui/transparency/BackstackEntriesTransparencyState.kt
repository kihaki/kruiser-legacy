package de.gaw.kruiser.backstack.ui.transparency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

val LocalTransparencyState = compositionLocalOf<BackstackEntriesTransparencyState?> { null }

interface BackstackEntriesTransparencyState {
    val transparentEntries: StateFlow<BackstackEntries>

    fun registerAsTransparent(entry: BackstackEntry)
    fun unregisterAsTransparent(entry: BackstackEntry)
}

internal class DefaultBackstackEntriesTransparencyState : BackstackEntriesTransparencyState {
    override val transparentEntries = MutableStateFlow<BackstackEntries>(emptyList())

    override fun registerAsTransparent(entry: BackstackEntry) = transparentEntries.update {
        it + entry
    }

    override fun unregisterAsTransparent(entry: BackstackEntry) = transparentEntries.update {
        it - entry
    }
}

/**
 * Convenience method for creating transparent [BackstackEntry]s, see [RegisterAsTransparentEffect]
 * for details.
 */
@Composable
fun TransparentScreen(content: @Composable () -> Unit) {
    RegisterAsTransparentEffect()
    content()
}

/**
 * Registers the provided [BackstackEntry] as transparent, so the [BackstackEntry] below will still
 * be drawn instead of being culled.
 */
@Composable
fun RegisterAsTransparentEffect(
    backstackEntry: BackstackEntry = LocalBackstackEntry.currentOrThrow,
    transparencyState: BackstackEntriesTransparencyState? = LocalTransparencyState.current,
) = DisposableEffect(Unit) {
    transparencyState?.registerAsTransparent(backstackEntry)
    onDispose {
        transparencyState?.unregisterAsTransparent(backstackEntry)
    }
}