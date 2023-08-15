package de.gaw.kruiser.remoteui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpSize

/**
 * Defines the RemoteUi layout, such as the RemoteUi position, size and how often it is on screen.
 * // TODO: Make onScreenCount a list of destinations instead
 */
data class RemoteUiLayout(
    val onScreenCount: Int = 0,
    val size: DpSize = DpSize.Zero,
    val position: Offset = Offset.Zero,
) {
    val isVisible: Boolean get() = onScreenCount > 0
}

typealias RemoteUiKey = Any

val LocalRemoteUiCoordinator =
    compositionLocalOf<RemoteUiCoordinator> { DefaultRemoteUiCoordinator() }

/**
 * Stores and handles the RemoteUi layouts.
 */
interface RemoteUiCoordinator {
    val layouts: Map<RemoteUiKey, RemoteUiLayout>

    fun updatePosition(key: RemoteUiKey, offset: Offset)
    fun updateSize(key: RemoteUiKey, size: DpSize)
    fun updateVisibility(key: RemoteUiKey, isVisible: Boolean)
}

private class DefaultRemoteUiCoordinator : RemoteUiCoordinator {
    override val layouts = SnapshotStateMap<RemoteUiKey, RemoteUiLayout>()

    private fun update(key: RemoteUiKey, block: RemoteUiLayout.() -> RemoteUiLayout) {
        layouts[key] = (layouts[key] ?: RemoteUiLayout()).block()
    }

    override fun updatePosition(key: RemoteUiKey, offset: Offset) =
        update(key) { copy(position = offset) }

    override fun updateSize(key: RemoteUiKey, size: DpSize) =
        update(key) { copy(size = size) }

    override fun updateVisibility(key: RemoteUiKey, isVisible: Boolean) = when {
        isVisible -> update(key) { copy(onScreenCount = onScreenCount + 1) }
        else -> update(key) { copy(onScreenCount = onScreenCount - 1) }
    }
}

@Composable
internal fun RemoteUiCoordinator.collectLayout(key: RemoteUiKey) = remember(this, key) {
    derivedStateOf { layouts[key] ?: RemoteUiLayout() }
}