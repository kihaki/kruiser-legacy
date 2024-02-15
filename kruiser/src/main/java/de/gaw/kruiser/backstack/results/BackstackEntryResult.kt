package de.gaw.kruiser.backstack.results

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.reflect.KClass

typealias BackstackResult = Any

inline fun <reified T : BackstackResult> BackstackEntryResult(
    result: T,
    marker: String? = null,
): BackstackEntryResult<T> = BackstackEntryResult(
    marker = marker,
    result = result,
    resultType = result::class,
)

data class BackstackEntryResult<T : BackstackResult>(
    val resultType: KClass<out T>,
    val result: T,
    val marker: String?,
)

interface BackstackResultsStore {
    val results: StateFlow<Set<BackstackEntryResult<*>>>

    fun mutate(block: Set<BackstackEntryResult<*>>.() -> Set<BackstackEntryResult<*>>)
}

inline fun <reified T : BackstackResult> BackstackResultsStore.setOrMutateResult(
    default: T,
    marker: String? = null,
    crossinline block: T.() -> T,
) = mutate {
    var mutated = false
    map {
        if (it.resultType == T::class && it.marker == marker) {
            mutated = true
            (it as BackstackEntryResult<T>).copy(result = block(it.result))
        } else {
            it
        }
    }.run {
        if (!mutated) {
            this + BackstackEntryResult(default, marker)
        } else {
            this
        }
    }.toSet()
}

inline fun <reified T : BackstackResult> BackstackResultsStore.setResult(
    result: T,
    marker: String? = null,
) = mutate { this + BackstackEntryResult(marker = marker, result = result) }

inline fun <reified T : BackstackResult> BackstackResultsStore.clearResult(
    result: BackstackEntryResult<T>,
) = clearResult(clazz = T::class, marker = result.marker)

inline fun <reified T : BackstackResult> BackstackResultsStore.clearResult(
    clazz: KClass<T>,
    marker: String? = null,
) = mutate { filter { it.resultType == clazz && it.marker == marker }.toSet() }

val LocalBackstackEntriesResultsStore = compositionLocalOf<BackstackResultsStore?> { null }

@Composable
fun rememberSaveableBackstackResultsStore(): BackstackResultsStore = rememberSaveable(
    saver = backstackResultsStoreSaver,
) {
    BackstackResultsStoreImpl()
}

// Assumes the results are at least serializable
private val backstackResultsStoreSaver = Saver<BackstackResultsStore, Set<BackstackEntryResult<*>>>(
    save = { it.results.value },
    restore = { BackstackResultsStoreImpl(initialResults = it) }
)

internal class BackstackResultsStoreImpl(
    initialResults: Set<BackstackEntryResult<*>> = emptySet(),
) : BackstackResultsStore {
    override val results = MutableStateFlow(initialResults)

    override fun mutate(block: Set<BackstackEntryResult<*>>.() -> Set<BackstackEntryResult<*>>) {
        results.update(block)
    }
}

@Composable
inline fun <reified T : BackstackResult> rememberResult(
    marker: String? = null,
    store: BackstackResultsStore = LocalBackstackEntriesResultsStore.currentOrThrow,
): State<T?> = produceState(
    key1 = store,
    key2 = marker,
    initialValue = store.results.value.findMine<T>(marker)?.result,
) {
    store.results
        .map { results ->
            results.findMine<T>(marker)
        }.collectLatest {
            value = it?.result
//            it?.let { result -> store.clearResult(result) }
        }
}

inline fun <reified T : BackstackResult> Set<BackstackEntryResult<*>>.findMine(marker: String? = null) =
    firstOrNull { entry ->
        entry.resultType == T::class && entry.marker == marker
    } as BackstackEntryResult<T>?
