package de.gaw.kruiser.backstack.results

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.reflect.KClass

inline fun <reified T : Any> BackstackEntryResult(
    result: T,
    marker: String? = null,
) = BackstackEntryResult(
    marker = marker,
    result = result,
    resultType = result::class,
)

data class BackstackEntryResult<T : Any>(
    val resultType: KClass<out T>,
    val result: T,
    val marker: String?,
)

interface BackstackResultsStore {
    val results: StateFlow<Set<BackstackEntryResult<*>>>

    fun <T : Any> setResult(result: BackstackEntryResult<T>)
    fun <T : Any> clearResult(result: BackstackEntryResult<T>)
}

inline fun <reified T : Any> BackstackResultsStore.setResult(result: T, marker: String? = null) =
    setResult(BackstackEntryResult(marker = marker, result = result))

inline fun <reified T : Any> BackstackResultsStore.clearResult(result: T, marker: String? = null) =
    clearResult(BackstackEntryResult(marker = marker, result = result))

val LocalBackstackEntriesResultsStore = compositionLocalOf<BackstackResultsStore?> { null }

internal class BackstackResultsStoreImpl : BackstackResultsStore {
    override val results = MutableStateFlow(emptySet<BackstackEntryResult<*>>())

    override fun <T : Any> setResult(result: BackstackEntryResult<T>) = results.update {
        it + result
    }

    override fun <T : Any> clearResult(result: BackstackEntryResult<T>) = results.update {
        it - result
    }
}

@Composable
inline fun <reified T : Any> rememberResult(
    marker: String? = null,
    store: BackstackResultsStore = LocalBackstackEntriesResultsStore.currentOrThrow,
): State<T?> {
    val state = remember { mutableStateOf(store.results.value.findMine<T>(marker)?.result) }
    LaunchedEffect(store, marker) {
        store.results.map { results ->
            results.findMine<T>(marker)
        }.collectLatest {
            state.value = it?.result
            it?.let { store.clearResult(it) }
        }
    }
    return state
}

inline fun <reified T : Any> Set<BackstackEntryResult<*>>.findMine(marker: String? = null) =
    firstOrNull { entry ->
        entry.resultType == T::class && entry.marker == marker
    } as BackstackEntryResult<T>?
