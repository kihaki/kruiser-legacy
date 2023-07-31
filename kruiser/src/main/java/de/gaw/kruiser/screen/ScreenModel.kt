package de.gaw.kruiser.screen

import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.Closeable

interface ScreenModel : Closeable {
    @CallSuper
    override fun close() {
        closeCoroutineScope()
    }
}

private val screenCoroutinesScopeRegistry = mutableMapOf<ScreenModel, CoroutineScope>()
private fun ScreenModel.closeCoroutineScope() =
    screenCoroutinesScopeRegistry
        .remove(this)
        ?.cancel()

val ScreenModel.scope: CoroutineScope
    get() = screenCoroutinesScopeRegistry.getOrPut(this) {
        CoroutineScope(SupervisorJob())
    }