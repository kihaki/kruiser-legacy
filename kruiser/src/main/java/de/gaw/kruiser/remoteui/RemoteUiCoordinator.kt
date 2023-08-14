package de.gaw.kruiser.remoteui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpSize

data class RemoteUiLayout(
    val onScreenCount: Int = 0,
    val size: DpSize = DpSize.Zero,
    val position: Offset = Offset.Zero,
) {
    val isVisible: Boolean get() = onScreenCount > 0
}

typealias RemoteUiKey = Any

val LocalRemoteUiCoordinator = compositionLocalOf { RemoteUiCoordinator() }

class RemoteUiCoordinator {
    val layouts = SnapshotStateMap<RemoteUiKey, RemoteUiLayout>()

    private fun update(key: RemoteUiKey, block: RemoteUiLayout.() -> RemoteUiLayout) {
        layouts[key] = (layouts[key] ?: RemoteUiLayout()).block()
    }

    fun updatePosition(key: RemoteUiKey, offset: Offset) =
        update(key) { copy(position = offset) }

    fun updateSize(key: RemoteUiKey, size: DpSize) =
        update(key) { copy(size = size) }

    fun updateVisibility(key: RemoteUiKey, isVisible: Boolean) = when {
        isVisible -> update(key) { copy(onScreenCount = onScreenCount + 1) }
        else -> update(key) { copy(onScreenCount = onScreenCount - 1) }
    }

}